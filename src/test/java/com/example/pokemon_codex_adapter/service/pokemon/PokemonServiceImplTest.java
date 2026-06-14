package com.example.pokemon_codex_adapter.service.pokemon;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.PokemonInfoDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationContentsDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationRequestDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationResponseDto;
import com.example.pokemon_codex_adapter.mapper.PokemonInfoMapper;
import com.example.pokemon_codex_adapter.mapper.TranslationMapper;
import com.example.pokemon_codex_adapter.service.translation.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.pokemon_codex_adapter.exception.custom.PokemonApiException;
import com.example.pokemon_codex_adapter.exception.custom.PokemonNotFoundException;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    private static final String POKEMON_INFO_PATH = "/pokemon-species/{name}";
    private static final String CAVE_HABITAT = "cave";
    private static final String MEWTWO = "mewtwo";
    private static final String PIKACHU = "pikachu";
    private static final String ZUBAT = "zubat";
    private static final String UNKNOWNMON = "unknownmon";
    private static final String A_DESCRIPTION = "A description.";

    @Mock private RestClient pokemonRestClient;
    @Mock private PokemonInfoMapper pokemonInfoMapper;
    @Mock private TranslationService translationService;
    @Mock private TranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private PokemonServiceImpl pokemonService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pokemonService, "getPokemonInfoPath", POKEMON_INFO_PATH);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getPokemonInfo_shouldCallApiAndReturnMappedDto() {
        PokemonInfoDto apiDto = new PokemonInfoDto();
        PokemonInfoLocalDto localDto = buildLocalDto(MEWTWO, A_DESCRIPTION, "rare", true);

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(pokemonRestClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PokemonInfoDto.class)).thenReturn(apiDto);
        when(pokemonInfoMapper.toLocalDto(apiDto)).thenReturn(localDto);

        PokemonInfoLocalDto result = pokemonService.getPokemonInfo(MEWTWO);

        assertThat(result).isEqualTo(localDto);
        verify(pokemonInfoMapper).toLocalDto(apiDto);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getPokemonInfo_whenApiReturns404_shouldThrowPokemonNotFoundException() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(pokemonRestClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PokemonInfoDto.class)).thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> pokemonService.getPokemonInfo(UNKNOWNMON))
                .isInstanceOf(PokemonNotFoundException.class)
                .hasMessageContaining(UNKNOWNMON);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getPokemonInfo_whenApiReturnsGenericError_shouldThrowPokemonApiException() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(pokemonRestClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PokemonInfoDto.class)).thenThrow(new RestClientException("Server error"));

        assertThatThrownBy(() -> pokemonService.getPokemonInfo(MEWTWO))
                .isInstanceOf(PokemonApiException.class)
                .hasMessageContaining(MEWTWO);
    }

    @Test
    void getTranslatedPokemonInfo_whenCaveHabitat_shouldCallYoda() {
        PokemonInfoLocalDto localDto = buildLocalDto(ZUBAT, A_DESCRIPTION, CAVE_HABITAT, false);
        doReturn(localDto).when(pokemonService).getPokemonInfo(ZUBAT);
        when(translationService.translateYoda(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo(ZUBAT);

        verify(translationService).translateYoda(new TranslationRequestDto(A_DESCRIPTION));
        verify(translationService, never()).translateShakespeare(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenLegendary_shouldCallYoda() {
        PokemonInfoLocalDto localDto = buildLocalDto(MEWTWO, A_DESCRIPTION, "rare", true);
        doReturn(localDto).when(pokemonService).getPokemonInfo(MEWTWO);
        when(translationService.translateYoda(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo(MEWTWO);

        verify(translationService).translateYoda(new TranslationRequestDto(A_DESCRIPTION));
        verify(translationService, never()).translateShakespeare(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenNeitherCaveNorLegendary_shouldCallShakespeare() {
        PokemonInfoLocalDto localDto = buildLocalDto(PIKACHU, A_DESCRIPTION, "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo(PIKACHU);
        when(translationService.translateShakespeare(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo(PIKACHU);

        verify(translationService).translateShakespeare(new TranslationRequestDto(A_DESCRIPTION));
        verify(translationService, never()).translateYoda(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenDescriptionIsNull_shouldSkipTranslation() {
        PokemonInfoLocalDto localDto = buildLocalDto(PIKACHU, null, "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo(PIKACHU);

        pokemonService.getTranslatedPokemonInfo(PIKACHU);

        verifyNoInteractions(translationService);
    }

    @Test
    void getTranslatedPokemonInfo_whenDescriptionIsBlank_shouldSkipTranslation() {
        PokemonInfoLocalDto localDto = buildLocalDto(PIKACHU, "   ", "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo(PIKACHU);

        pokemonService.getTranslatedPokemonInfo(PIKACHU);

        verifyNoInteractions(translationService);
    }

    @Test
    void getTranslatedPokemonInfo_whenTranslationFails_shouldReturnOriginalDescription() {
        PokemonInfoLocalDto localDto = buildLocalDto(PIKACHU, "Original description.", "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo(PIKACHU);
        when(translationService.translateShakespeare(any())).thenThrow(new RestClientException("API error"));

        PokemonInfoLocalDto result = pokemonService.getTranslatedPokemonInfo("pikachu");

        assertThat(result.getDescription()).isEqualTo("Original description.");
        verify(translationMapper, never()).applyTranslation(any(), any());
    }

    private PokemonInfoLocalDto buildLocalDto(String name, String description, String habitat, Boolean isLegendary) {
        return PokemonInfoLocalDto.builder()
                .name(name)
                .description(description)
                .habitat(habitat)
                .isLegendary(isLegendary)
                .build();
    }

    private TranslationResponseDto buildTranslationResponse(String translated) {
        TranslationContentsDto contents = new TranslationContentsDto();
        contents.setTranslated(translated);
        TranslationResponseDto response = new TranslationResponseDto();
        response.setContents(contents);
        return response;
    }

}
