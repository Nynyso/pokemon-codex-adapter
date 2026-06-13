package com.example.pokemon_codex_adapter.service.pokemon;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.PokemonInfoDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationRequestDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationResponseDto;
import com.example.pokemon_codex_adapter.mapper.PokemonInfoMapper;
import com.example.pokemon_codex_adapter.mapper.TranslationMapper;
import com.example.pokemon_codex_adapter.service.translation.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.pokemon_codex_adapter.exception.custom.PokemonApiException;
import com.example.pokemon_codex_adapter.exception.custom.PokemonNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PokemonServiceImpl implements PokemonService {

    private final RestClient pokemonRestClient;
    private final PokemonInfoMapper pokemonInfoMapper;
    private final TranslationService translationService;
    private final TranslationMapper translationMapper;

    @Value("${pokemon.api.paths.get-pokemon-info}")
    private String getPokemonInfoPath;

    @Override
    public PokemonInfoLocalDto getPokemonInfo(String name) {
        try {
            PokemonInfoDto pokemonInfoDto = pokemonRestClient.get()
                    .uri(getPokemonInfoPath, name)
                    .retrieve()
                    .body(PokemonInfoDto.class);
            return pokemonInfoMapper.toLocalDto(pokemonInfoDto);
        } catch (HttpClientErrorException.NotFound e) {
            throw new PokemonNotFoundException(name);
        } catch (RestClientException e) {
            throw new PokemonApiException(name);
        }
    }

    @Override
    public PokemonInfoLocalDto getTranslatedPokemonInfo(String name) {
        PokemonInfoLocalDto pokemonInfo = getPokemonInfo(name);

        if (pokemonInfo.getDescription() == null || pokemonInfo.getDescription().isBlank()) {
            return pokemonInfo;
        }

        try {
            TranslationRequestDto request = new TranslationRequestDto(pokemonInfo.getDescription());
            TranslationResponseDto translationResponse;

            if ("cave".equals(pokemonInfo.getHabitat()) || Boolean.TRUE.equals(pokemonInfo.getIsLegendary())) {
                translationResponse = translationService.translateYoda(request);
            } else {
                translationResponse = translationService.translateShakespeare(request);
            }

            translationMapper.applyTranslation(pokemonInfo, translationResponse);
        } catch (RestClientException e) {
            log.warn("Translation failed for pokemon '{}', keeping original description. Reason: {}", name, e.getMessage());
        }

        return pokemonInfo;
    }

}
