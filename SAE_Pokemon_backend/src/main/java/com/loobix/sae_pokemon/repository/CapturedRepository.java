package com.loobix.sae_pokemon.repository;

import com.loobix.sae_pokemon.model.Captured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CapturedRepository extends JpaRepository<Captured, Long> {

    boolean existsByIdGameAndNumeroAndIsShinyAndUserId(Integer idGame, Integer numero, Boolean isShiny, String userId);

    List<Captured> findByIdGameAndUserId(Integer idGame, String userId);

    void deleteByIdGameAndNumeroAndIsShinyAndUserId(Integer idGame, Integer numero, Boolean isShiny, String userId);}