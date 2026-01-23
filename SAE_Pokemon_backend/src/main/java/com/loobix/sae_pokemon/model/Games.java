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
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "region_name")
    private String regionName;
}