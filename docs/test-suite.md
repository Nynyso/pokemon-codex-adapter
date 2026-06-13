# Test Suite

Unit tests for the Pokemon Codex Adapter. All tests use **JUnit 5** and **Mockito**, with **AssertJ** for assertions. No Spring context is loaded unless specified.

---

## Configuration

The Mockito byte-buddy agent is registered as a JVM `-javaagent` in `maven-surefire-plugin` to avoid dynamic agent loading warnings on JDK 25:

```xml
<argLine>-javaagent:.../mockito-core-${mockito.version}.jar</argLine>
```

---

## Test Classes

### `PokemonServiceImplTest`
**Type:** Unit — `@ExtendWith(MockitoExtension.class)`
**Focus:** Business logic in `PokemonServiceImpl`

All mocked dependencies: `RestClient`, `PokemonInfoMapper`, `TranslationService`, `TranslationMapper`.
A `@Spy` on the service allows stubbing `getPokemonInfo` internally for translation tests, avoiding RestClient chain mocking in every test.
`@Value`-injected fields are set via `ReflectionTestUtils.setField`.

| Test | Description |
|---|---|
| `getPokemonInfo_shouldCallApiAndReturnMappedDto` | Calls the PokéAPI chain and returns the mapped local DTO |
| `getPokemonInfo_whenApiReturns404_shouldThrowPokemonNotFoundException` | Throws `PokemonNotFoundException` when the API returns 404 |
| `getPokemonInfo_whenApiReturnsGenericError_shouldThrowPokemonApiException` | Throws `PokemonApiException` on any other `RestClientException` |
| `getTranslatedPokemonInfo_whenCaveHabitat_shouldCallYoda` | Routes to Yoda translation when habitat is `cave` |
| `getTranslatedPokemonInfo_whenLegendary_shouldCallYoda` | Routes to Yoda translation when `isLegendary` is `true` |
| `getTranslatedPokemonInfo_whenNeitherCaveNorLegendary_shouldCallShakespeare` | Routes to Shakespeare when habitat is not `cave` and not legendary |
| `getTranslatedPokemonInfo_whenDescriptionIsNull_shouldSkipTranslation` | Skips translation entirely when description is `null` |
| `getTranslatedPokemonInfo_whenDescriptionIsBlank_shouldSkipTranslation` | Skips translation entirely when description is blank |
| `getTranslatedPokemonInfo_whenTranslationFails_shouldReturnOriginalDescription` | Returns the original description when the translation API throws `RestClientException` |

---

### `PokemonInfoMapperTest`
**Type:** Unit — plain JUnit 5, no Mockito
**Focus:** Custom `firstEnglishFlavorText` default method in `PokemonInfoMapper`

The interface is instantiated as an anonymous class overriding only `toLocalDto` (not under test), allowing the `default` method to be exercised directly without a Spring context or MapStruct generation.

| Test | Description |
|---|---|
| `firstEnglishFlavorText_shouldReturnFirstEnglishEntry` | Returns the first entry whose language is `en` |
| `firstEnglishFlavorText_shouldNormalizeWhitespace` | Replaces `\n`, `\f`, `\r` with spaces and trims the result |
| `firstEnglishFlavorText_whenNoEnglishEntry_shouldReturnNull` | Returns `null` when no English entry is present |
| `firstEnglishFlavorText_whenListIsNull_shouldReturnNull` | Returns `null` on a null input list |
| `firstEnglishFlavorText_whenListIsEmpty_shouldReturnNull` | Returns `null` on an empty input list |

---

### `PokemonControllerTest`
**Type:** Unit — `@ExtendWith(MockitoExtension.class)` with `MockMvcBuilders.standaloneSetup`
**Focus:** Endpoint wiring and response structure in `PokemonController`

Uses `standaloneSetup` to avoid the Spring Boot test autoconfigure dependency. `GlobalExceptionHandler` is registered manually via `setControllerAdvice`. `PokemonService` is mocked with `@Mock`.

| Test | Description |
|---|---|
| `getPokemonInfo_shouldReturn200WithDto` | `GET /pokemon/mewtwo` returns 200 with all fields correctly serialized |
| `getTranslatedPokemonInfo_shouldReturn200WithDto` | `GET /pokemon/translated/mewtwo` returns 200 with the translated DTO |

---

### `GlobalExceptionHandlerTest`
**Type:** Unit — plain JUnit 5 with `MockMvcBuilders.standaloneSetup`
**Focus:** HTTP error responses produced by `GlobalExceptionHandler`

Uses a minimal `FakeController` inner class that deliberately throws each custom exception, allowing the handler to be tested in full isolation without loading any real controllers or services.

| Test | Description |
|---|---|
| `whenPokemonNotFoundException_shouldReturn404` | Returns `404` with `"Not Found"` error and the pokemon name in the message |
| `whenPokemonApiException_shouldReturn502` | Returns `502` with `"Bad Gateway"` error and the pokemon name in the message |

---

### `PokemonCodexAdapterApplicationTests`
**Type:** Integration — `@SpringBootTest`
**Focus:** Spring context loads without errors

Smoke test that verifies the full application context starts successfully.
