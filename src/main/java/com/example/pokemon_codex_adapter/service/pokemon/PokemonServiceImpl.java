package com.example.pokemon_codex_adapter.service.pokemon;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.PokemonInfoDto;
import com.example.pokemon_codex_adapter.mapper.PokemonInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class PokemonServiceImpl implements PokemonService {

    private final RestClient pokemonRestClient;
    private final PokemonInfoMapper pokemonInfoMapper;

    @Override
    public PokemonInfoLocalDto getPokemonInfo(String name) {
        PokemonInfoDto pokemonInfoDto = pokemonRestClient.get()
                .uri("/pokemon-species/{name}", name)
                .retrieve()
                .body(PokemonInfoDto.class);
        return pokemonInfoMapper.toLocalDto(pokemonInfoDto);
    }

}
