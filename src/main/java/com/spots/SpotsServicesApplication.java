package com.spots;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import java.io.File;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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

    public static final String IMAGE_DIR =
            new File(BASE_DIR, IMAGES_FOLDER_NAME).getPath() + File.separator;;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpotsServicesApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpotsServicesApplication.class, args);
    }
}
