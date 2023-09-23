package com.spots.common.output;

import com.spots.domain.Role;
import com.spots.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private long id;
    private Role role;
    private String username;
    private String picture;
    private LocalDateTime nextRandomSpotGeneratedTime;
    private long currentSpotId;
    private List<Long> conqueredSpots;
    private String email;
    private String password;
    private boolean emailVerified;
    private long timeUntilNextRoll;

    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .picture(user.getPicture())
                .nextRandomSpotGeneratedTime(user.getNextRandomSpotGeneratedTime())
                .currentSpotId(user.getCurrentSpotId())
                .conqueredSpots(user.getConqueredSpots())
                .email(user.getEmail())
                .password(user.getPassword())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}
