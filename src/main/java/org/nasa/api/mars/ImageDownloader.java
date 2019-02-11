package org.nasa.api.mars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ImageDownloader {
    public static void main(String[] args ) {
    	SpringApplication.run(ImageDownloader.class, args);
    }
}
