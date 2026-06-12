package com.example.pokemon_codex_adapter.controller;

import com.example.pokemon_codex_adapter.constants.PokemonCodexAdapterRoutes;
import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.service.pokemon.PokemonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PokemonCodexAdapterRoutes.POKEMON_ROOT)
@RequiredArgsConstructor
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping(PokemonCodexAdapterRoutes.GET_POKEMON_INFO)
    public ResponseEntity<PokemonInfoLocalDto> getPokemonInfo(@PathVariable String pokemonName) {
        return ResponseEntity.ok(pokemonService.getPokemonInfo(pokemonName));
    }

    @GetMapping(PokemonCodexAdapterRoutes.GET_TRANSLATED_POKEMON_INFO)
    public ResponseEntity<Void> getTranslatedPokemonInfo(@PathVariable String pokemonName) {
        return ResponseEntity.ok().build();
    }

}
