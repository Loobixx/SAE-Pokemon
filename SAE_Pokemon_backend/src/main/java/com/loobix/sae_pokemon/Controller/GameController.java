package com.loobix.sae_pokemon.controller;

import java.util.List;

import com.loobix.sae_pokemon.dto.CaptureRequest;
import com.loobix.sae_pokemon.dto.WishRequest;
import com.loobix.sae_pokemon.model.Captured;
import com.loobix.sae_pokemon.model.Games;
import com.loobix.sae_pokemon.model.User;
import com.loobix.sae_pokemon.model.Wished;
import com.loobix.sae_pokemon.repository.CapturedRepository;
import com.loobix.sae_pokemon.repository.GamesRepository;
import com.loobix.sae_pokemon.repository.WishedRepository;
import com.loobix.sae_pokemon.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    // Accès aux utilisateurs
    @Autowired
    UserRepository userRepository;

    // Accès aux Pokémon capturés
    @Autowired
    CapturedRepository capturedRepository;

    // Accès aux souhaits
    @Autowired
    WishedRepository wishedRepository;

    /**
     * Capture d’un Pokémon
     */
    @PostMapping("/capture")
    @Transactional
    public ResponseEntity<?> capturePokemon(@RequestBody CaptureRequest request) {

        // Récupération de l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        // Vérification utilisateur + partie active
        if (user == null || user.getLastGame() == 0)
            return ResponseEntity.badRequest().build();

        // Vérifie si le Pokémon est déjà capturé
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        if (alreadyCaught) {
            return ResponseEntity.badRequest().body("Déjà attrapé !");
        }

        // Sauvegarde de la capture
        Captured captured = new Captured(
                request.getNumero(),
                user.getLastGame(),
                request.getIsShiny(),
                authentication.getName()
        );
        capturedRepository.save(captured);

        // Suppression du Pokémon de la wishlist s’il y était
        List<Wished> wishes = wishedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        for (Wished w : wishes) {
            boolean memeNumero = w.getNumero().equals(request.getNumero());
            boolean memeShiny = (w.getIsShiny() == request.getIsShiny());

            if (memeNumero && memeShiny) {
                wishedRepository.delete(w);
            }
        }

        return ResponseEntity.ok("Pokémon capturé et retiré des souhaits !");
    }

    /**
     * Liste des Pokémon capturés
     */
    @GetMapping("/captured")
    public ResponseEntity<List<Captured>> getCapturedPokemon() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        // Aucun utilisateur ou aucune partie
        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of());
        }

        // Récupération des captures pour le jeu courant
        List<Captured> capturedList = capturedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        return ResponseEntity.ok(capturedList);
    }

    /**
     * Ajout d’un Pokémon à la wishlist
     */
    @PostMapping("/wish")
    public ResponseEntity<String> wishPokemon(@RequestBody WishRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0) {
            return ResponseEntity.badRequest().body("Erreur: Pas de partie en cours.");
        }

        // Empêche d’ajouter un Pokémon déjà capturé
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        if (alreadyCaught) {
            return ResponseEntity.badRequest()
                    .body("Inutile de le souhaiter, vous l'avez déjà attrapé !");
        }

        // Vérifie si le souhait existe déjà
        List<Wished> wishes = wishedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        boolean alreadyWished = wishes.stream().anyMatch(w ->
                w.getNumero().equals(request.getNumero())
                        && w.getIsShiny() == request.getIsShiny()
        );

        if (alreadyWished) {
            return ResponseEntity.badRequest()
                    .body("Ce Pokémon est déjà dans votre liste de souhaits !");
        }

        Wished newWish = new Wished(
                request.getNumero(),
                user.getLastGame(),
                request.getIsShiny(),
                authentication.getName()
        );

        wishedRepository.save(newWish);

        return ResponseEntity.ok("Pokémon ajouté aux souhaits !");
    }

    /**
     * Liste des Pokémon souhaités
     */
    @GetMapping("/wished")
    public ResponseEntity<List<Wished>> getWishedPokemon() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of());
        }

        List<Wished> wishedList = wishedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        return ResponseEntity.ok(wishedList);
    }

    /**
     * Suppression d’une capture
     */
    @DeleteMapping("/capture")
    @Transactional
    public ResponseEntity<?> removeCapture(@RequestBody CaptureRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0)
            return ResponseEntity.badRequest().build();

        capturedRepository.deleteByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        return ResponseEntity.ok("Pokémon relâché !");
    }

    /**
     * Suppression d’un souhait
     */
    @DeleteMapping("/wish")
    @Transactional
    public ResponseEntity<?> removeWish(@RequestBody WishRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0)
            return ResponseEntity.badRequest().build();

        wishedRepository.deleteByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        return ResponseEntity.ok("Retiré des souhaits !");
    }

    // Accès aux jeux disponibles
    @Autowired
    private GamesRepository gameRepository;

    /**
     * Liste de tous les jeux
     */
    @GetMapping("/list")
    public ResponseEntity<List<Games>> getAllGames() {
        return ResponseEntity.ok(gameRepository.findAll());
    }
}
