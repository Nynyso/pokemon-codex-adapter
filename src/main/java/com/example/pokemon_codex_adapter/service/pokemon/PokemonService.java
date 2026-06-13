package com.example.pokemon_codex_adapter.service.pokemon;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;

public interface PokemonService {

    PokemonInfoLocalDto getPokemonInfo(String name);

    PokemonInfoLocalDto getTranslatedPokemonInfo(String name);

}
