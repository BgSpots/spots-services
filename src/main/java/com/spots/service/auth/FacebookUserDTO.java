package com.spots.service.auth;

import java.time.Duration;
import lombok.Data;

@Data
public class FacebookUserDTO {
    private String id;
    private String name;
    private PictureDataDTO picture;
    private String email;
    private String jwtToken;
    private Duration timeUntilNextRoll;

    // Define the inner class for "PictureDataDTO"
    @Data
    public static class PictureDataDTO {
        private PictureDTO data;
    }

    @Data
    public static class PictureDTO {
        private int height;
        private boolean isSilhouette;
        private String url;
        private int width;
    }
}
