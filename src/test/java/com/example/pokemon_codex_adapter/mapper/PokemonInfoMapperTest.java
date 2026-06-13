package com.example.pokemon_codex_adapter.mapper;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.FlavorTextEntryDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.NamedResourceDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.PokemonInfoDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonInfoMapperTest {

    private final PokemonInfoMapper mapper = new PokemonInfoMapper() {
        @Override
        public PokemonInfoLocalDto toLocalDto(PokemonInfoDto dto) {
            return null;
        }
    };

    @Test
    void firstEnglishFlavorText_shouldReturnFirstEnglishEntry() {
        List<FlavorTextEntryDto> entries = List.of(
                buildEntry("Texto en español.", "es"),
                buildEntry("Some English text.", "en")
        );

        String result = mapper.firstEnglishFlavorText(entries);

        assertThat(result).isEqualTo("Some English text.");
    }

    @Test
    void firstEnglishFlavorText_shouldNormalizeWhitespace() {
        List<FlavorTextEntryDto> entries = List.of(
                buildEntry("Line one.\nLine two.\fLine three.", "en")
        );

        String result = mapper.firstEnglishFlavorText(entries);

        assertThat(result).isEqualTo("Line one. Line two. Line three.");
    }

    @Test
    void firstEnglishFlavorText_whenNoEnglishEntry_shouldReturnNull() {
        List<FlavorTextEntryDto> entries = List.of(
                buildEntry("Texto en español.", "es"),
                buildEntry("Testo in italiano.", "it")
        );

        assertThat(mapper.firstEnglishFlavorText(entries)).isNull();
    }

    @Test
    void firstEnglishFlavorText_whenListIsNull_shouldReturnNull() {
        assertThat(mapper.firstEnglishFlavorText(null)).isNull();
    }

    @Test
    void firstEnglishFlavorText_whenListIsEmpty_shouldReturnNull() {
        assertThat(mapper.firstEnglishFlavorText(List.of())).isNull();
    }

    private FlavorTextEntryDto buildEntry(String text, String languageCode) {
        NamedResourceDto language = new NamedResourceDto();
        language.setName(languageCode);

        FlavorTextEntryDto entry = new FlavorTextEntryDto();
        entry.setFlavorText(text);
        entry.setLanguage(language);
        return entry;
    }

}
