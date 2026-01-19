package com.loobix.sae_pokemon.controller;

import com.loobix.sae_pokemon.model.Pokemon;
import com.loobix.sae_pokemon.repository.PokemonRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pokemons")
@CrossOrigin(origins = "*") // Autorise ton fichier HTML local à accéder au serveur
public class PokemonController {

    private final PokemonRepository repository;

    public PokemonController(PokemonRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Pokemon> getAllPokemons() {
        // Retourne la liste stockée dans PostgreSQL (triée par ID pour faire propre)
        return repository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Pokemon getPokemonById(@PathVariable Long id) {
        // On cherche dans la base, si on trouve pas on renvoie null (ou une erreur 404 idéalement)
        return repository.findById(id).orElse(null);
    }
}