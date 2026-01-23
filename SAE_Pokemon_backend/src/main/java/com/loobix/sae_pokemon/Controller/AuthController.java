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
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsById(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Erreur: Email déjà utilisé !");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPseudo(signUpRequest.getPseudo());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        user.setVolume(50);
        user.setLastGame(1);

        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getUsername(),
                userDetails.getPseudo()
        ));
    }
}