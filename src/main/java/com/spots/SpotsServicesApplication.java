package com.spots;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@OpenAPIDefinition
public class SpotsServicesApplication extends SpringBootServletInitializer {
    private static final String IMAGES_FOLDER_NAME = "images";
    private static final File BASE_DIR;

    static {
        try {
            BASE_DIR = new File(".").getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException("Error getting the base directory", e);
        }
    }

    public static String IMAGE_DIR;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpotsServicesApplication.class);
    }
    public static void main(String[] args) throws IOException {
        IMAGE_DIR = new File(BASE_DIR, IMAGES_FOLDER_NAME).getPath()+File.separator;
        SpringApplication.run(SpotsServicesApplication.class, args);
    }
}
