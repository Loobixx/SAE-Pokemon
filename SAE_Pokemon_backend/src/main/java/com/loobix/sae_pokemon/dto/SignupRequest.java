package com.loobix.sae_pokemon.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String pseudo;
    private String password;
}