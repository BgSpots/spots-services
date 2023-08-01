package com.spots.service.auth;

import lombok.Data;

@Data
public class FacebookUserDTO {
    private String id;
    private String name;
    private PictureDataDTO picture;
    private String email;
    private String jwt;

    // Define the inner class for "PictureDataDTO"
    @Data
    public static class PictureDataDTO {
        private int height;
        private boolean isSilhouette;
        private String url;
        private int width;
    }
}
