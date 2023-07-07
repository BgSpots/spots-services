package com.spots.domain;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
