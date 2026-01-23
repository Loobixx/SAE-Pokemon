package com.loobix.sae_pokemon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Games {

    @Id
    // On n'utilise pas @GeneratedValue car on veut utiliser les IDs officiels de l'API (1, 2, 3...)
    private Integer id;

    @Column(name = "name")
    private String name; // ex: "generation-i", "generation-ii"

    // On ajoute un champ "region" pour faire joli plus tard (Kanto, Johto...)
    @Column(name = "region_name")
    private String regionName;
}