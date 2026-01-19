package com.loobix.sae_pokemon.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    private Long id;
    private String name;
    private String type;

    @Column(length = 2048)
    private String imageUrl;      // Image Normale

    @Column(length = 2048)
    private String shinyImageUrl; // Image Shiny (NOUVEAU)

    public Pokemon() {}

    // Constructeur mis à jour avec le nouveau champ
    public Pokemon(Long id, String name, String type, String imageUrl, String shinyImageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.shinyImageUrl = shinyImageUrl;
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getImageUrl() { return imageUrl; }
    public String getShinyImageUrl() { return shinyImageUrl; }
}