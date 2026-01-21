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
    private final ObjectMapper mapper;

    public DataInitializer(PokemonRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            System.out.println("⚡ Base vide. Démarrage de l'importation (Normal + Shiny)...");
            importerLesDoublons();
        } else {
            System.out.println("✅ Base déjà remplie. Pas d'import nécessaire.");
        }
    }

    private void importerLesDoublons() {
        // On commence par la première page (20 par défaut, ou plus si tu veux)
        String nextUrl = "https://pokeapi.co/api/v2/pokemon?limit=100";

        try {
            while (nextUrl != null && !nextUrl.equals("null")) {
                System.out.println("🌐 Récupération de la page : " + nextUrl);
                String jsonResponse = restTemplate.getForObject(nextUrl, String.class);
                JsonNode root = mapper.readTree(jsonResponse);

                // On met à jour l'URL de la page suivante pour la prochaine itération
                nextUrl = root.path("next").asText();

                JsonNode results = root.path("results");
                for (JsonNode node : results) {
                    String urlDetail = node.path("url").asText();

                    // Ton code de traitement reste le même
                    PokemonDetails details = recupererDetails(urlDetail);

                    // On ignore les formes spéciales (ID > 10000) si tu ne veux que les vrais Pokémon
                    if (details.id > 10000) continue;

                    String description = recupererDescription(details.id);
                    String nameFR = recupererNomFR(details.id);

                    // Sauvegarde Normal
                    Pokemon p = new Pokemon();
                    p.setNumero(details.id.intValue());
                    p.setName(nameFR);
                    p.setType(details.type);
                    p.setShiny(false);
                    p.setImageUrl(details.imageNormal);
                    p.setDescription(description);

                    repository.save(p);
                    // Sauvegarde Shiny
                    Pokemon p_shiny = new Pokemon();
                    p_shiny.setNumero(details.id.intValue());
                    p_shiny.setName(nameFR);
                    p_shiny.setType(details.type);
                    p_shiny.setShiny(true);
                    p_shiny.setImageUrl(details.imageShiny);
                    p_shiny.setDescription(description);

                    repository.save(p_shiny);

                    System.out.println("--> Importé : " + nameFR + " (#" + details.id + ")");
                }
            }
            System.out.println("🎉 Tous les Pokémon ont été importés !");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'import : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- CLASSES ET MÉTHODES UTILITAIRES ---

    private record PokemonDetails(Long id, String type, String imageNormal, String imageShiny) {}

    private PokemonDetails recupererDetails(String url) {
        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(json);

            Long id = root.path("id").asLong();

            // Récupération du premier type
            String type = root.path("types").get(0).path("type").path("name").asText();

            // Images Officielles
            String normal = root.path("sprites").path("other").path("official-artwork").path("front_default").asText();
            String shiny = root.path("sprites").path("other").path("official-artwork").path("front_shiny").asText();

            if (shiny == null || shiny.isEmpty() || shiny.equals("null")) {
                shiny = root.path("sprites").path("front_shiny").asText();
            }

            return new PokemonDetails(id, type, normal, shiny);
        } catch (Exception e) {
            return new PokemonDetails(0L, "inconnu", "", "");
        }
    }

    private String recupererNomFR(Long id) {
        try {
            String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + id;
            String json = restTemplate.getForObject(speciesUrl, String.class);
            JsonNode root = mapper.readTree(json);
            JsonNode names = root.path("names");

            for (JsonNode entry : names) {
                if (entry.path("language").path("name").asText().equals("fr")) {
                    return entry.path("name").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur récupération nom FR pour ID " + id);
        }
        return "Nom indisponible";
    }


    private String recupererDescription(Long id) {
        try {
            String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + id;
            String json = restTemplate.getForObject(speciesUrl, String.class);
            JsonNode root = mapper.readTree(json);
            JsonNode flavorEntries = root.path("flavor_text_entries");

            for (JsonNode entry : flavorEntries) {
                if (entry.path("language").path("name").asText().equals("fr")) {
                    return entry.path("flavor_text").asText()
                            .replace("\n", " ")
                            .replace("\f", " ");
                }
            }
        } catch (Exception e) {
            System.err.println("Pas de description pour ID " + id);
        }
        return "Pas de description disponible.";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}