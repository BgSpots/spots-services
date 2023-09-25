package com.spots.dataInserter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spots.common.input.SpotDto;
import com.spots.common.input.UserBody;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.spots.InvalidSpotNameException;
import com.spots.service.spots.SpotsService;
import com.spots.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class DataInserter {
    private static  SpotsService spotsService;

    private static UserService userService;

    @Autowired
    public DataInserter(SpotsService spotsService,UserService userService) {
        this.spotsService = spotsService;
        this.userService = userService;
    }


    public static void addData() throws InvalidSpotNameException, InvalidInputException, IOException {

        if(spotsService.getSpots().isEmpty()){
            ObjectMapper objectMapper = new ObjectMapper();
            List<SpotDto> spots = objectMapper.readValue(new File("src/main/java/com/spots/dataInserter/data.json"), new TypeReference<>() {});
            for (SpotDto spot : spots) {
                spotsService.createSpot(spot);
            }

        }
        if(userService.getUsers().isEmpty()){
            UserBody userBody = new UserBody();
            userBody.setEmail("admin913151@mail.bg");
            userBody.setUsername("admin");
            userBody.setPassword("password");
            userService.createUser(userBody);

        }


    }
}
