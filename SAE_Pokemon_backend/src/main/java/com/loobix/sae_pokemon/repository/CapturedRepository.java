package com.loobix.sae_pokemon.repository;

import com.loobix.sae_pokemon.model.Captured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CapturedRepository extends JpaRepository<Captured, Long> {
    List<Captured> findByIdGame(Integer idGame);

    boolean existsByIdGameAndNumeroAndIsShiny(Integer idGame, Integer numero, Boolean isShiny);

    boolean existsByIdGameAndNumero(Integer idGame, Integer numero);

    void deleteByIdGameAndNumeroAndIsShiny(Integer idGame, Integer numero, Boolean isShiny);
}