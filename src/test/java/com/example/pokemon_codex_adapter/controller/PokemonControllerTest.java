package com.example.pokemon_codex_adapter.controller;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.exception.GlobalExceptionHandler;
import com.example.pokemon_codex_adapter.service.pokemon.PokemonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PokemonControllerTest {

    @Mock private PokemonService pokemonService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PokemonController(pokemonService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getPokemonInfo_shouldReturn200WithDto() throws Exception {
        PokemonInfoLocalDto dto = PokemonInfoLocalDto.builder()
                .name("mewtwo")
                .description("A scientific experiment gone wrong.")
                .habitat("rare")
                .isLegendary(true)
                .build();
        when(pokemonService.getPokemonInfo("mewtwo")).thenReturn(dto);

        mockMvc.perform(get("/pokemon/mewtwo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mewtwo"))
                .andExpect(jsonPath("$.habitat").value("rare"))
                .andExpect(jsonPath("$.isLegendary").value(true))
                .andExpect(jsonPath("$.description").value("A scientific experiment gone wrong."));
    }

    @Test
    void getTranslatedPokemonInfo_shouldReturn200WithDto() throws Exception {
        PokemonInfoLocalDto dto = PokemonInfoLocalDto.builder()
                .name("mewtwo")
                .description("Translated description.")
                .habitat("rare")
                .isLegendary(true)
                .build();
        when(pokemonService.getTranslatedPokemonInfo("mewtwo")).thenReturn(dto);

        mockMvc.perform(get("/pokemon/translated/mewtwo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mewtwo"))
                .andExpect(jsonPath("$.description").value("Translated description."));
    }

}
