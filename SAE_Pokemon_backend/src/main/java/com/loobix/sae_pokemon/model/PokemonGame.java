package com.loobix.sae_pokemon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pokemonGame")
public class PokemonGame {
    @Id
    private Integer id_pokemon;

    private Integer id_game;
}
