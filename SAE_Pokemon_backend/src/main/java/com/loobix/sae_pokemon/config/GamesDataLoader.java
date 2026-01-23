package com.loobix.sae_pokemon.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loobix.sae_pokemon.model.Games;
import com.loobix.sae_pokemon.repository.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GamesDataLoader implements CommandLineRunner {

    @Autowired
    private com.loobix.sae_pokemon.repository.GamesRepository GamesRepository;

    @Override
    public void run(String... args) throws Exception {
        // On vérifie si la base est déjà remplie pour ne pas spammer l'API
        if (GamesRepository.count() > 0) {
            System.out.println("✅ Les jeux (Générations) sont déjà chargés en BDD.");
            return;
        }

        System.out.println("⏳ Récupération des Générations depuis PokéAPI...");

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // On boucle sur les 9 générations actuelles
        for (int i = 1; i <= 9; i++) {
            try {
                String url = "https://pokeapi.co/api/v2/generation/" + i;
                String jsonResponse = restTemplate.getForObject(url, String.class);
                JsonNode root = mapper.readTree(jsonResponse);

                Games Games = new Games();
                Games.setId(i); // ID officiel (1, 2, 3...)

                // Récupération du nom (ex: generation-i)
                String name = root.path("name").asText();
                Games.setName(name);

                // Récupération de la région principale (ex: kanto)
                String region = root.path("main_region").path("name").asText();
                Games.setRegionName(region);

                GamesRepository.save(Games);
                System.out.println("   -> Sauvegardé : " + name + " (" + region + ")");

            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de la gen " + i + ": " + e.getMessage());
            }
        }
        System.out.println("✅ Importation des jeux terminée !");
    }
}