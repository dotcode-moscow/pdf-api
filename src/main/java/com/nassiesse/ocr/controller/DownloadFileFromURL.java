package com.nassiesse.ocr.controller;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadFileFromURL {

    public static void main(URL source, String filename) throws MalformedURLException {
        try {
            downloadUsingNIO(source, filename);
            //downloadUsingStream(source, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadUsingStream(URL urlStr, String file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(urlStr.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static void downloadUsingNIO(URL urlStr, String file) throws IOException {
        ReadableByteChannel rbc = Channels.newChannel(urlStr.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

}

