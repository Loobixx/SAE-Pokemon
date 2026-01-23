package com.loobix.sae_pokemon.repository;

import com.loobix.sae_pokemon.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Games, Integer> {
}