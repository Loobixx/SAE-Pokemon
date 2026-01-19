package com.loobix.sae_pokemon.config;

import com.loobix.sae_pokemon.model.Pokemon;
import com.loobix.sae_pokemon.repository.PokemonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PokemonRepository repository;
    private final RestTemplate restTemplate;

    public DataInitializer(PokemonRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        long count = repository.count();
        if (count == 0) {
            System.out.println("⚡ Base vide. Récupération des données et des images Shiny...");
            importerLesPokemons();
        } else {
            System.out.println("✅ Base déjà remplie (" + count + " Pokémons).");
        }
    }

    private void importerLesPokemons() {
        String apiUrl = "https://pokeapi.co/api/v2/pokemon?limit=151";

        try {
            String jsonResponse = restTemplate.getForObject(apiUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            for (JsonNode node : results) {
                String name = node.path("name").asText();
                String urlDetail = node.path("url").asText();

                // Récupération de l'ID
                String[] segments = urlDetail.split("/");
                Long id = Long.parseLong(segments[segments.length - 1]);

                // --- Appel API Détail pour avoir les images exactes ---
                PokemonDetails details = recupererDetails(urlDetail);

                // Création avec les DEUX images
                Pokemon p = new Pokemon(id, name, details.type, details.imageNormal, details.imageShiny);

                repository.save(p);
                System.out.println("--> Sauvegardé : " + name + " (Normal + Shiny)");
            }
            System.out.println("🎉 Importation terminée !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Petite classe interne pour transporter les infos temporairement
    private record PokemonDetails(String type, String imageNormal, String imageShiny) {}

    private PokemonDetails recupererDetails(String urlDetail) {
        try {
            // On appelle l'URL spécifique du Pokémon (ex: .../pokemon/1/)
            String json = restTemplate.getForObject(urlDetail, String.class);
            JsonNode root = new ObjectMapper().readTree(json);

            // 1. Le Type
            String type = root.path("types").get(0).path("type").path("name").asText();

            // 2. Image Normale (Official Artwork HD)
            String normal = root.path("sprites").path("other").path("official-artwork").path("front_default").asText();

            // 3. Image Shiny (Official Artwork Shiny)
            String shiny = root.path("sprites").path("other").path("official-artwork").path("front_shiny").asText();
            System.out.println(shiny);
            // Fallback : Si l'artwork shiny n'existe pas, on prend le sprite pixel art classique
            if (shiny == null || shiny.equals("null") || shiny.isEmpty()) {
                shiny = root.path("sprites").path("front_shiny").asText();
            }

            return new PokemonDetails(type, normal, shiny);
        } catch (Exception e) {
            return new PokemonDetails("normal", "", "");
        }
    }
}