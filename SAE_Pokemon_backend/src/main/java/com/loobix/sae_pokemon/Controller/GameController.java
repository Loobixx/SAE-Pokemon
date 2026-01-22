package com.loobix.sae_pokemon.controller;

import java.util.List;
import com.loobix.sae_pokemon.dto.CaptureRequest;
import com.loobix.sae_pokemon.dto.WishRequest;
import com.loobix.sae_pokemon.model.Captured;
import com.loobix.sae_pokemon.model.User;
import com.loobix.sae_pokemon.model.Wished;
import com.loobix.sae_pokemon.repository.CapturedRepository;
import com.loobix.sae_pokemon.repository.WishedRepository;
import com.loobix.sae_pokemon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CapturedRepository capturedRepository;

    @Autowired
    WishedRepository wishedRepository;

    @PostMapping("/capture")
    @Transactional // Important pour autoriser la suppression
    public ResponseEntity<?> capturePokemon(@RequestBody CaptureRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        // 1. On vérifie si on l'a déjà (Logique existante)
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShiny(
                user.getLastGame(), request.getNumero(), request.getIsShiny()
        );

        if (alreadyCaught) {
            return ResponseEntity.badRequest().body("Déjà attrapé !");
        }

        // 2. On Sauvegarde la capture (Logique existante)
        Captured captured = new Captured(request.getNumero(), user.getLastGame(), request.getIsShiny());
        capturedRepository.save(captured);

        // --- NETTOYAGE DES SOUHAITS ---
        List<Wished> wishes = wishedRepository.findByIdGame(user.getLastGame());
        for (Wished w : wishes) {
            // CORRECTION ICI : On vérifie le numéro ET le shiny
            // On utilise "booleanValue()" ou une comparaison directe si c'est des types primitifs
            boolean memeNumero = w.getNumero().equals(request.getNumero());
            boolean memeShiny = (w.getIsShiny() == request.getIsShiny());

            if (memeNumero && memeShiny) {
                wishedRepository.delete(w);
            }
        }

        return ResponseEntity.ok("Pokémon capturé et retiré des souhaits !");
    }

    @GetMapping("/captured")
    public ResponseEntity<?> getCapturedPokemon() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findById(email).orElse(null);

        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of()); // Retourne une liste vide si pas de partie
        }

        // 2. Récupération de la liste via le Repository
        List<Captured> capturedList = capturedRepository.findByIdGame(user.getLastGame());

        return ResponseEntity.ok(capturedList);
    }


    @PostMapping("/wish")
    public ResponseEntity<?> wishPokemon(@RequestBody WishRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0) {
            return ResponseEntity.badRequest().body("Erreur: Pas de partie en cours.");
        }

        // 1. D'ABORD : Vérifier si on l'a déjà CAPTURÉ (dans la même version)
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShiny(
                user.getLastGame(), request.getNumero(), request.getIsShiny()
        );
        if (alreadyCaught) {
            return ResponseEntity.badRequest().body("Inutile de le souhaiter, vous l'avez déjà attrapé !");
        }

        List<Wished> wishes = wishedRepository.findByIdGame(user.getLastGame());
        boolean alreadyWished = wishes.stream().anyMatch(w ->
                w.getNumero().equals(request.getNumero()) && w.getIsShiny() == request.getIsShiny()
        );

        if (alreadyWished) {
            return ResponseEntity.badRequest().body("Ce Pokémon est déjà dans votre liste de souhaits !");
        }

        // 3. Sauvegarder
        Wished newWish = new Wished(
                request.getNumero(),
                user.getLastGame(),
                request.getIsShiny()
        );

        wishedRepository.save(newWish);

        return ResponseEntity.ok("Pokémon ajouté aux souhaits !");
    }

    @GetMapping("/wished")
    public ResponseEntity<?> getWishedPokemon() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findById(email).orElse(null);

        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of()); // Retourne une liste vide si pas de partie
        }

        // 2. Récupération de la liste via le Repository
        List<Wished> wishedList = wishedRepository.findByIdGame(user.getLastGame());

        return ResponseEntity.ok(wishedList);
    }

    @DeleteMapping("/capture")
    @Transactional
    public ResponseEntity<?> removeCapture(@RequestBody CaptureRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);
        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        capturedRepository.deleteByIdGameAndNumeroAndIsShiny(
                user.getLastGame(), request.getNumero(), request.getIsShiny()
        );

        return ResponseEntity.ok("Pokémon relâché !");
    }

    @DeleteMapping("/wish")
    @Transactional
    public ResponseEntity<?> removeWish(@RequestBody WishRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);
        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        wishedRepository.deleteByIdGameAndNumeroAndIsShiny(
                user.getLastGame(), request.getNumero(), request.getIsShiny()
        );

        return ResponseEntity.ok("Retiré des souhaits !");
    }
}