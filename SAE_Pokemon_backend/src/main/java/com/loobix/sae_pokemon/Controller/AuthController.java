package com.loobix.sae_pokemon.controller;

import com.loobix.sae_pokemon.config.UserDetailsImpl;
import com.loobix.sae_pokemon.dto.JwtResponse;
import com.loobix.sae_pokemon.dto.LoginRequest;
import com.loobix.sae_pokemon.dto.SignupRequest;
import com.loobix.sae_pokemon.model.User;
import com.loobix.sae_pokemon.repository.UserRepository;
import com.loobix.sae_pokemon.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        // 1. Vérification si l'email existe déjà
        if (userRepository.existsById(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Erreur: Email déjà utilisé !");
        }

        // 2. Création de l'entité User à partir du DTO
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPseudo(signUpRequest.getPseudo());
        // ENCODER le mot de passe est obligatoire pour que Security l'accepte
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        // Valeurs par défaut
        user.setVolume(50);
        user.setLastGame(0);

        // 3. SAUVEGARDE RÉELLE DANS POSTGRES
        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 4. On renvoie le DTO avec les 4 arguments requis par ton constructeur
        return ResponseEntity.ok(new JwtResponse(
                jwt,                        // token
                "Bearer",                   // type
                userDetails.getUsername(),  // email (identifiant)
                userDetails.getPseudo()     // pseudo
        ));
    }
}