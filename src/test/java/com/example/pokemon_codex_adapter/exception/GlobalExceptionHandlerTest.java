package com.example.pokemon_codex_adapter.exception;

import com.example.pokemon_codex_adapter.exception.custom.PokemonApiException;
import com.example.pokemon_codex_adapter.exception.custom.PokemonNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private static final String NOT_FOUND_POKEMON = "missingno";
    private static final String API_ERROR_POKEMON = "mewtwo";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FakeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void whenPokemonNotFoundException_shouldReturn404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Pokemon '" + NOT_FOUND_POKEMON + "' was not found."));
    }

    @Test
    void whenPokemonApiException_shouldReturn502() throws Exception {
        mockMvc.perform(get("/test/api-error"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message").value("An error occurred while retrieving information for pokemon '" + API_ERROR_POKEMON + "'."));
    }

    @RestController
    static class FakeController {

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new PokemonNotFoundException(NOT_FOUND_POKEMON);
        }

        @GetMapping("/test/api-error")
        public void apiError() {
            throw new PokemonApiException(API_ERROR_POKEMON);
        }

    }

}
