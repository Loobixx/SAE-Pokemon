package com.loobix.sae_pokemon.model;

import jakarta.persistence.*;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "pokemon", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"numero", "isShiny"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero; // ex: 25
    private String name;

    private String type;
    private boolean isShiny; // ex: true

    @Column(length = 2048)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;



}