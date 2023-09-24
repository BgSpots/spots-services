package com.spots.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@Document
public class User implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
