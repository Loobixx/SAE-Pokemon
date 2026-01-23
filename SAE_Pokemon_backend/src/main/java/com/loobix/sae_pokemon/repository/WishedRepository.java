package com.loobix.sae_pokemon.repository;

import com.loobix.sae_pokemon.model.Wished;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishedRepository extends JpaRepository<Wished, Long> {
    List<Wished> findByIdGameAndUserId(Integer idGame, String userId);

    void deleteByIdGameAndNumeroAndIsShinyAndUserId(Integer idGame, Integer numero, Boolean isShiny, String userId);
}