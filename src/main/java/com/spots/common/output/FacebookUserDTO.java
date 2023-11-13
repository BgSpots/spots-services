package com.spots.common.output;

import java.time.Duration;
import lombok.Data;

@Data
public class FacebookUserDTO {
    private long id;
    private String name;
    private PictureDataDTO picture;
    private String email;
    private String jwtToken;
    private Duration timeUntilNextRoll;
    private long currentSpotId;

    @Data
    public static class PictureDataDTO {
        private PictureDTO data;
    }

    @Data
    public static class PictureDTO {
        private int height;
        private boolean isSilhouette;
        private String url;
        private String picture;
        private int width;
    }
}
