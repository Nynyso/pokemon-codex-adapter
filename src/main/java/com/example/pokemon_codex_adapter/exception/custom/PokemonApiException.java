package com.example.pokemon_codex_adapter.exception.custom;

public class PokemonApiException extends RuntimeException {

    public PokemonApiException(String name) {
        super("An error occurred while retrieving information for pokemon '" + name + "'.");
    }

}
