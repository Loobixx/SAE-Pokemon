package com.loobix.sae_pokemon.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "HomePage"; // renvoie HomePage.html
    }
}