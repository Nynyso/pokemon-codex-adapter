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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock private RestClient pokemonRestClient;
    @Mock private PokemonInfoMapper pokemonInfoMapper;
    @Mock private TranslationService translationService;
    @Mock private TranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private PokemonServiceImpl pokemonService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pokemonService, "getPokemonInfoPath", "/pokemon-species/{name}");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getPokemonInfo_shouldCallApiAndReturnMappedDto() {
        PokemonInfoDto apiDto = new PokemonInfoDto();
        PokemonInfoLocalDto localDto = buildLocalDto("mewtwo", "A description.", "rare", true);

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(pokemonRestClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PokemonInfoDto.class)).thenReturn(apiDto);
        when(pokemonInfoMapper.toLocalDto(apiDto)).thenReturn(localDto);

        PokemonInfoLocalDto result = pokemonService.getPokemonInfo("mewtwo");

        assertThat(result).isEqualTo(localDto);
        verify(pokemonInfoMapper).toLocalDto(apiDto);
    }

    @Test
    void getTranslatedPokemonInfo_whenCaveHabitat_shouldCallYoda() {
        PokemonInfoLocalDto localDto = buildLocalDto("zubat", "A description.", "cave", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo("zubat");
        when(translationService.translateYoda(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo("zubat");

        verify(translationService).translateYoda(new TranslationRequestDto("A description."));
        verify(translationService, never()).translateShakespeare(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenLegendary_shouldCallYoda() {
        PokemonInfoLocalDto localDto = buildLocalDto("mewtwo", "A description.", "rare", true);
        doReturn(localDto).when(pokemonService).getPokemonInfo("mewtwo");
        when(translationService.translateYoda(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo("mewtwo");

        verify(translationService).translateYoda(new TranslationRequestDto("A description."));
        verify(translationService, never()).translateShakespeare(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenNeitherCaveNorLegendary_shouldCallShakespeare() {
        PokemonInfoLocalDto localDto = buildLocalDto("pikachu", "A description.", "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo("pikachu");
        when(translationService.translateShakespeare(any())).thenReturn(buildTranslationResponse("Translated."));

        pokemonService.getTranslatedPokemonInfo("pikachu");

        verify(translationService).translateShakespeare(new TranslationRequestDto("A description."));
        verify(translationService, never()).translateYoda(any());
    }

    @Test
    void getTranslatedPokemonInfo_whenDescriptionIsNull_shouldSkipTranslation() {
        PokemonInfoLocalDto localDto = buildLocalDto("pikachu", null, "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo("pikachu");

        pokemonService.getTranslatedPokemonInfo("pikachu");

        verifyNoInteractions(translationService);
    }

    @Test
    void getTranslatedPokemonInfo_whenDescriptionIsBlank_shouldSkipTranslation() {
        PokemonInfoLocalDto localDto = buildLocalDto("pikachu", "   ", "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo("pikachu");

        pokemonService.getTranslatedPokemonInfo("pikachu");

        verifyNoInteractions(translationService);
    }

    @Test
    void getTranslatedPokemonInfo_whenTranslationFails_shouldReturnOriginalDescription() {
        PokemonInfoLocalDto localDto = buildLocalDto("pikachu", "Original description.", "forest", false);
        doReturn(localDto).when(pokemonService).getPokemonInfo("pikachu");
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
