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
import org.apache.pdfbox.tools.PDFToImage;
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
//			PDFRenderer pdfRenderer = new PDFRenderer(document);
//			BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
//
//			// ресайз фото
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			Thumbnails.of(bim)
//					.size(640, 480)
//					.outputFormat("JPEG")
//					.outputQuality(1)
////					.toFile(new File("src/main/resources/images/" + UUID.randomUUID() + ".jpeg"));
//					.toOutputStream(outputStream);
//
//			byte[] encodeBase64 = Base64.encodeBase64(outputStream.toByteArray());
//			String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
//
//			String src = "data:image/jpeg;base64," + base64Encoded;
//
			obj.put("img", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/4QB2RXhpZgAATU0AKgAAAAgAAwESAAMAAAABAAEAAAExAAIAAAARAAAAModpAAQAAAABAAAARAAAAAB3d3cuaW5rc2NhcGUub3JnAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAAIKADAAQAAAABAAAAIAAAAAD/7QA4UGhvdG9zaG9wIDMuMAA4QklNBAQAAAAAAAA4QklNBCUAAAAAABDUHYzZjwCyBOmACZjs+EJ+/8AAEQgAIAAgAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/bAEMAAQEBAQEBAgEBAgMCAgIDBAMDAwMEBgQEBAQEBgcGBgYGBgYHBwcHBwcHBwgICAgICAkJCQkJCwsLCwsLCwsLC//bAEMBAgICAwMDBQMDBQsIBggLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLC//dAAQAAv/aAAwDAQACEQMRAD8A/Wv/AIKS/tI6f+2z+0x4d/Yv0e38GeNvgr4d8c/CyTxPJ9pbUZ9Rn8S6lqUYtNsTPamGNdP/AHySfMd4x7ebfB39kn4HftF+Bl+LXwO/4Jw/CXUfCl7f6laafd3/AIktLK5nTTryazaR4P7Gl8ss8DHb5jYBHJr4j/4JtfCG81DQPhVF8KPDLStHo37Muv6uNKs8kJHeeJGub248peiqAZZn6AAsa+x/gN+wh8T/ANqf4fy/Hv4F/BzwHoXhXxBrWuPZWd38T/HOnzr9n1K6gld7axU2sTSyxvIVh+QbuAOlAHsv/DtrRf8ApGr8Gf8AwrbX/wCUVfBf7dv7M37MPgT4DfGf4QfEf9iv4efBzxnbfBzxX498NeIfD2pW+szRSaFLZWrAFNOs2glWS/jkjcM33DwODXrviH4P/Bv4YeAvivp3x68MeFPBHjL4XeLNG8PzXmp/Ffx1H4buodZ0uLUkKTiRrr7QFlC7DAFO1juAUbvnrVk/Zy8QfsaftWeKfh3c/DXVPEdn8FfElot14T8c+KPFeqRWU7W7yxtDr0SxRWzyRxM8kTb/ADEjBBBJAB//0Pq3/glX8Y7b4N6N8Op7rS5dUHibwJ+zd4eUQ389gYG1C68SIJ2MBBmWMjLQSZilHyuCvFft5/wSK+N/wX8L/sDeFNB8TeL9E06+ttW8ULLbXWoQQyxk67qBAZHcMMgg8joa/mn+Fvxj/Ze/ZP0r4GeAfj38S9J8J/ELTdS+EfhnxN4V1pZNOu/D7fDG41qbUrq7knCxi2mW7T7LIDiclfL3hgR9Y+PtW/4Jf+M/HeteMLf9qX9liKPVr+5vFS7+F+h3twonkZwJbh9TVppBn5pWALtliATQB9xeNfG2ua58Vf2uvFPwK1vXp2m+IHgxUvfA/iHQ9FuWjXwvYq4+3a1vsvLDDDov70sAF4DV8qfG/Xfjjqv/AATx/arh+JuqfEW/sV+E+vGNPGHjLwl4ksll2rgxwaAi3Mcu3diSQ+Tt3A/MUrk7fx/+wba/Dy4+EVr+2T+zFH4Tu71NTn0VPhjow06S9jTy1uHthq/lNMqfKJCu4LwDiuH8b6p+xpL+y/8AGj4Gfsz/ABh+DfxV+Ifxb8GXvgrw74d+FXgOx0HW7nUNVeNIvMm067upGtVI3TiRFijA81nUJyAf/9k=");

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

