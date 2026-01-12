package com.loobix.sae_pokemon;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Autorise le frontend à nous parler
public class TestController {

    // 1. Une petite classe interne pour représenter un Pokémon
    // (Tu pourras la mettre dans un fichier à part plus tard)
    static class Pokemon {
        public int id;
        public String name;
        public String type;

        public Pokemon(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }

    private List<Pokemon> pokedex = new ArrayList<>();

    public TestController() {
        pokedex.add(new Pokemon(1, "Bulbizarre", "Plante"));
        pokedex.add(new Pokemon(4, "Salamèche", "Feu"));
        pokedex.add(new Pokemon(7, "Carapuce", "Eau"));
        pokedex.add(new Pokemon(25, "Pikachu", "Electrik"));
        pokedex.add(new Pokemon(143, "Ronflex", "Normal"));
        pokedex.add(new Pokemon(150, "Mewtwo", "Psy"));
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Connexion réussie !";
    }

    // 👇 C'est ICI que le Front va récupérer la liste
    @GetMapping("/pokemons")
    public List<Pokemon> getPokemons() {
        return pokedex;
    }
}