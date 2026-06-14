package com.example.pokemon_codex_adapter.controller;

import com.example.pokemon_codex_adapter.constants.PokemonCodexAdapterRoutes;
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

    private static final String MEWTWO = "mewtwo";
    private static final String GET_POKEMON_INFO_URL = PokemonCodexAdapterRoutes.POKEMON_ROOT + "/" + MEWTWO;
    private static final String GET_TRANSLATED_POKEMON_INFO_URL = PokemonCodexAdapterRoutes.POKEMON_ROOT + "/translated/" + MEWTWO;

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
                .name(MEWTWO)
                .description("A scientific experiment gone wrong.")
                .habitat("rare")
                .isLegendary(true)
                .build();
        when(pokemonService.getPokemonInfo(MEWTWO)).thenReturn(dto);

        mockMvc.perform(get(GET_POKEMON_INFO_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(MEWTWO))
                .andExpect(jsonPath("$.habitat").value("rare"))
                .andExpect(jsonPath("$.isLegendary").value(true))
                .andExpect(jsonPath("$.description").value("A scientific experiment gone wrong."));
    }

    @Test
    void getTranslatedPokemonInfo_shouldReturn200WithDto() throws Exception {
        PokemonInfoLocalDto dto = PokemonInfoLocalDto.builder()
                .name(MEWTWO)
                .description("Translated description.")
                .habitat("rare")
                .isLegendary(true)
                .build();
        when(pokemonService.getTranslatedPokemonInfo(MEWTWO)).thenReturn(dto);

        mockMvc.perform(get(GET_TRANSLATED_POKEMON_INFO_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(MEWTWO))
                .andExpect(jsonPath("$.description").value("Translated description."));
    }

}
