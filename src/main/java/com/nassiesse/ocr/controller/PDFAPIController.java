package com.nassiesse.ocr.controller;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RestController
public class PDFAPIController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "displayImage";
	}

	@PostMapping("/api/extractText")
    public @ResponseBody ResponseEntity<String>
					extractTextFromPDFFile(@RequestParam("url") String url) {
		PDDocument document = null;
		boolean bool;
		System.out.println(url);
		try {
			URL source = new URL(url);
			String filename = FileUtils.getTempDirectory() + Paths.get(source.getPath()).getFileName().toString();

			DownloadFileFromURL.main(source, filename);
			File tempDestinationFile = FileUtils.getFile(filename);

			// Load file into PDFBox class
			document = PDDocument.load(tempDestinationFile);
			PDFTextStripper stripper = new PDFTextStripper();

			int count = document.getNumberOfPages();

			if (count <= 0) {
				return new ResponseEntity<>("error PDF file", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			JSONObject obj = new JSONObject();
			for(int i=1; i<=count; i++){
				stripper.setStartPage(i);
				stripper.setEndPage(i);
				String content = stripper.getText(document);

				obj.put(String.valueOf(i), content);
			}

			// Получим обложку
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

			// ресайз фото
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Thumbnails.of(bim)
					.size(640, 480)
					.outputFormat("JPEG")
					.outputQuality(1)
//					.toFile(new File("src/main/resources/images/" + UUID.randomUUID() + ".jpeg"));
					.toOutputStream(outputStream);

			byte[] encodeBase64 = Base64.encodeBase64(outputStream.toByteArray());
			String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);

			String src = "data:image/jpeg;base64," + base64Encoded;

			obj.put("img", src);

			bool = tempDestinationFile.delete();
			System.out.println("File deleted: "+ bool);
			document.close();
			return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (document != null) {
					document.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@GetMapping("/api/pdf/ping")
    public ResponseEntity<String> get()
    {
		return new ResponseEntity<>("PONG", HttpStatus.OK);
    }

	@PostMapping("/api/imageToPDF")
	public @ResponseBody ResponseEntity<String>
	convertPDFToImage(@RequestParam("url") String[] urls) {
		try {
			PDDocument document = new PDDocument();
			boolean bool;

			for (String url:
				 urls) {
				URL source = new URL(url);
				File tempDestinationFile =
						FileUtils.getFile(
								FileUtils.getTempDirectory(),
								new File(source.getFile()).getName());

				FileUtils.copyURLToFile(source, tempDestinationFile);

				InputStream in = new FileInputStream(tempDestinationFile);

				BufferedImage img_file = ImageIO.read(in);

				float width = img_file.getWidth();
				float height = img_file.getHeight();
				PDPage page = new PDPage(new PDRectangle(width, height));
				document.addPage(page);

				PDImageXObject  pdImageXObject = JPEGFactory.createFromImage(document, img_file, 1, 300);


				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				contentStream.drawImage(pdImageXObject, 0, 0);
				contentStream.close();
				in.close();

				bool = tempDestinationFile.delete();
				System.out.println("File deleted: "+ bool);
			}

			ByteArrayOutputStream bate_image = new ByteArrayOutputStream();
			document.save(bate_image);
			document.close();

			byte[] encodeBase64 = Base64.encodeBase64(bate_image.toByteArray());
			String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);

			String src = "data:application/pdf;base64," + base64Encoded;


			return new ResponseEntity<>(src, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}

