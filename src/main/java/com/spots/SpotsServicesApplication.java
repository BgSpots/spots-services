package com.spots;

import com.spots.dataInserter.DataInserter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@OpenAPIDefinition
public class SpotsServicesApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpotsServicesApplication.class, args);
        DataInserter.addData();
    }
}
