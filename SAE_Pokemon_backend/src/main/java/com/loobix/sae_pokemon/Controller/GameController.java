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
    @Transactional
    public ResponseEntity<?> capturePokemon(@RequestBody CaptureRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        // 1. VERIFICATION (Avec User ID)
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName() // On vérifie pour CET utilisateur
        );

        if (alreadyCaught) {
            return ResponseEntity.badRequest().body("Déjà attrapé !");
        }

        // 2. SAUVEGARDE (Avec User ID)
        Captured captured = new Captured(
                request.getNumero(),
                user.getLastGame(),
                request.getIsShiny(),
                authentication.getName() // On enregistre le propriétaire
        );
        capturedRepository.save(captured);

        // 3. NETTOYAGE DES SOUHAITS (Uniquement ceux de l'utilisateur)
        // On récupère uniquement les vœux de CE joueur
        List<Wished> wishes = wishedRepository.findByIdGameAndUserId(user.getLastGame(), authentication.getName());

        for (Wished w : wishes) {
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
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of());
        }

        // Récupération filtrée par USER ID
        List<Captured> capturedList = capturedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        return ResponseEntity.ok(capturedList);
    }

    @PostMapping("/wish")
    public ResponseEntity<?> wishPokemon(@RequestBody WishRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == 0) {
            return ResponseEntity.badRequest().body("Erreur: Pas de partie en cours.");
        }

        // 1. Vérifier si DÉJÀ CAPTURÉ par l'utilisateur
        boolean alreadyCaught = capturedRepository.existsByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(), request.getNumero(), request.getIsShiny(), authentication.getName()
        );
        if (alreadyCaught) {
            return ResponseEntity.badRequest().body("Inutile de le souhaiter, vous l'avez déjà attrapé !");
        }

        // 2. Vérifier si DÉJÀ SOUHAITÉ par l'utilisateur
        // On peut utiliser une méthode exists...AndUserId dans le repo pour faire plus propre,
        // ou filtrer la liste comme ici :
        List<Wished> wishes = wishedRepository.findByIdGameAndUserId(user.getLastGame(), authentication.getName());
        boolean alreadyWished = wishes.stream().anyMatch(w ->
                w.getNumero().equals(request.getNumero()) && w.getIsShiny() == request.getIsShiny()
        );

        if (alreadyWished) {
            return ResponseEntity.badRequest().body("Ce Pokémon est déjà dans votre liste de souhaits !");
        }

        // 3. SAUVEGARDE (Avec User ID)
        Wished newWish = new Wished(
                request.getNumero(),
                user.getLastGame(),
                request.getIsShiny(),
                authentication.getName() // Ajout du propriétaire
        );

        wishedRepository.save(newWish);

        return ResponseEntity.ok("Pokémon ajouté aux souhaits !");
    }

    @GetMapping("/wished")
    public ResponseEntity<?> getWishedPokemon() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);

        if (user == null || user.getLastGame() == null || user.getLastGame() == 0) {
            return ResponseEntity.ok(List.of());
        }

        // Récupération filtrée par USER ID
        List<Wished> wishedList = wishedRepository.findByIdGameAndUserId(
                user.getLastGame(),
                authentication.getName()
        );

        return ResponseEntity.ok(wishedList);
    }

    @DeleteMapping("/capture")
    @Transactional
    public ResponseEntity<?> removeCapture(@RequestBody CaptureRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);
        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        // SUPPRESSION CIBLÉE (Génération + Numéro + Shiny + USER)
        capturedRepository.deleteByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        return ResponseEntity.ok("Pokémon relâché !");
    }

    @DeleteMapping("/wish")
    @Transactional
    public ResponseEntity<?> removeWish(@RequestBody WishRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getName()).orElse(null);
        if (user == null || user.getLastGame() == 0) return ResponseEntity.badRequest().build();

        // SUPPRESSION CIBLÉE (Génération + Numéro + Shiny + USER)
        wishedRepository.deleteByIdGameAndNumeroAndIsShinyAndUserId(
                user.getLastGame(),
                request.getNumero(),
                request.getIsShiny(),
                authentication.getName()
        );

        return ResponseEntity.ok("Retiré des souhaits !");
    }


    @Autowired
    private GamesRepository gameRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Games>> getAllGames() {
        return ResponseEntity.ok(gameRepository.findAll());
    }
}