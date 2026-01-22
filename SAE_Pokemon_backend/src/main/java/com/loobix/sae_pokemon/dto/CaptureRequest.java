package com.loobix.sae_pokemon.dto;

import lombok.Data;

@Data
public class CaptureRequest {
    private Integer numero;
    private Boolean isShiny;
}