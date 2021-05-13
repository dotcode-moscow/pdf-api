package com.nassiesse.ocr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PDFAPIMicroserviceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(PDFAPIMicroserviceApplication.class, args);
	}

}
