package com.example.userservice.security.models;

import com.example.userservice.models.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

@JsonDeserialize
public class CustomGratedAuthority implements GrantedAuthority {

    private String authority;

    public CustomGratedAuthority() {
    }

    public CustomGratedAuthority(Role role){
        this.authority = role.getRoleName();
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
