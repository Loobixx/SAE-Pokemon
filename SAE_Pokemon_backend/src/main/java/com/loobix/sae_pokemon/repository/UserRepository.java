package com.loobix.sae_pokemon.repository;

import com.loobix.sae_pokemon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
