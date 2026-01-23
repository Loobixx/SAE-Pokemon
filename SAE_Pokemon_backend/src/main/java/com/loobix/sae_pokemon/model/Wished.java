package com.loobix.sae_pokemon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wished")
public class Wished {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "id_game")
    private Integer idGame;

    @Column(name = "is_shiny")
    private Boolean isShiny;

    @Column(name = "user_id")
    private String userId;

    public Wished(Integer numero, Integer idGame, Boolean isShiny, String userId) {
        this.numero = numero;
        this.idGame = idGame;
        this.isShiny = isShiny;
        this.userId = userId;
    }
}