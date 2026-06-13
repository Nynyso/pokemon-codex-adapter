package com.example.pokemon_codex_adapter.exception.custom;

public class PokemonNotFoundException extends RuntimeException {

    public PokemonNotFoundException(String name) {
        super("Pokemon '" + name + "' was not found.");
    }

}
