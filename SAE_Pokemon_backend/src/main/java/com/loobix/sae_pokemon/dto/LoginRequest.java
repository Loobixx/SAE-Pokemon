package com.loobix.sae_pokemon.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}