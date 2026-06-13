# Pokemon Codex Adapter

A Spring Boot 4.1.0 REST adapter that wraps the [PokéAPI](https://pokeapi.co) and the [Fun Translations API](https://api.funtranslations.mercxry.me) to serve enriched, optionally-translated Pokémon descriptions.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 4.1.0 | Application framework |
| Spring RestClient | HTTP client for external APIs |
| MapStruct 1.6.3 | Declarative object mapping |
| Lombok | Boilerplate reduction |
| Jakarta Validation | Input constraint enforcement |
| SLF4J | Logging |

---

## Configuration (`application.properties`)

| Property | Value |
|---|---|
| `pokemon.api.base-url` | `https://pokeapi.co/api/v2` |
| `pokemon.api.paths.get-pokemon-info` | `/pokemon-species/{name}` |
| `translation.api.base-url` | `https://api.funtranslations.mercxry.me/v1` |
| `translation.api.paths.translate-yoda` | `/translate/yoda` |
| `translation.api.paths.translate-shakespeare` | `/translate/shakespeare` |

---

## API Endpoints

### `GET /pokemon/{pokemonName}`
Returns the raw Pokémon information from PokéAPI mapped to a local DTO.

**Response**
```json
{
  "name": "mewtwo",
  "description": "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
  "habitat": "rare",
  "isLegendary": true
}
```

### `GET /pokemon/translated/{pokemonName}`
Returns the Pokémon information with the description translated. Translation strategy is chosen based on the Pokémon's attributes:

| Condition | Translation |
|---|---|
| `habitat == "cave"` or `isLegendary == true` | Yoda style |
| anything else | Shakespeare style |
| description is null/blank | no translation applied |
| translation API fails | original description returned |

**Validation** — both endpoints reject blank or null `pokemonName` with:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "getPokemonInfo.pokemonName: must not be blank"
}
```

---

## Project Structure

```
src/main/java/com/example/pokemon_codex_adapter/
│
├── PokemonCodexAdapterApplication.java       Entry point
│
├── config/
│   ├── PokemonConfig.java                    RestClient bean for PokéAPI (with request logger)
│   └── TranslationConfig.java               RestClient bean for Translation API (with request logger)
│
├── constants/
│   └── PokemonCodexAdapterRoutes.java        Route path constants
│
├── controller/
│   └── PokemonController.java               REST controller — 2 endpoints
│
├── service/
│   ├── pokemon/
│   │   ├── PokemonService.java              Interface
│   │   └── PokemonServiceImpl.java          Fetches Pokémon + orchestrates translation
│   └── translation/
│       ├── TranslationService.java          Interface
│       └── TranslationServiceImpl.java      Calls Yoda / Shakespeare endpoints
│
├── mapper/
│   ├── PokemonInfoMapper.java               PokemonInfoDto → PokemonInfoLocalDto
│   └── TranslationMapper.java              Applies translated text onto PokemonInfoLocalDto
│
├── dto/
│   ├── local/
│   │   └── PokemonInfoLocalDto.java         Internal Pokémon representation
│   ├── pokeapi/
│   │   ├── PokemonInfoDto.java              PokéAPI response (partial)
│   │   ├── FlavorTextEntryDto.java          Flavor text entry with language
│   │   └── NamedResourceDto.java            Generic { name, url } resource
│   └── translation/
│       ├── TranslationRequestDto.java       { "text": "..." }
│       ├── TranslationResponseDto.java      Full translation API response
│       ├── TranslationContentsDto.java      { translated, text, translation }
│       └── TranslationSuccessDto.java       { total }
│
└── exception/
    ├── GlobalExceptionHandler.java          @RestControllerAdvice — handles ConstraintViolationException
    └── ErrorResponse.java                   { status, error, message }
```

---

## Request Flow

```
Client
  │
  ▼
PokemonController          (@Validated — rejects blank names)
  │
  ▼
PokemonServiceImpl
  ├── pokemonRestClient → GET /pokemon-species/{name}  (PokéAPI)
  ├── PokemonInfoMapper → PokemonInfoLocalDto
  │
  └── [translated endpoint only]
        ├── if description blank → return as-is
        ├── if cave/legendary  → TranslationService.translateYoda()
        └── otherwise          → TranslationService.translateShakespeare()
              │
              ▼
        translationRestClient → POST /translate/yoda|shakespeare
              │
              ▼
        TranslationMapper → updates description in-place
        (on RestClientException → logs warning, returns original)
```
