# Pokemon Codex Adapter

A Spring Boot REST adapter that wraps the [PokéAPI](https://pokeapi.co) and the [Fun Translations API](https://api.funtranslations.mercxry.me) to serve enriched, optionally-translated Pokémon descriptions.

For full project and test suite documentation see the [`docs/`](docs/) folder.

---

## Requirements

### Run with Docker
- [Docker](https://www.docker.com/)

### Run independently
- [JDK 25](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)

### [Bruno](https://www.usebruno.com/) — to explore the API endpoints (or any API Client of your choosing)

---

## Run with Docker

### 1. Build the image

```bash
docker build -t pokemon-codex-adapter .
```

### 2. Start the container

```bash
docker run -p 8080:8080 --name pokemon-codex-adapter \
  -e POKEMON_API_BASE-URL=https://pokeapi.co/api/v2 \
  -e TRANSLATION_API_BASE-URL=https://api.funtranslations.mercxry.me/v1 \
  pokemon-codex-adapter
```

The application will be available at `http://localhost:8080`.

### 3. Stop the container

```bash
docker stop pokemon-codex-adapter
```

---

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/pokemon/{pokemonName}` | Returns Pokémon info |
| `GET` | `/pokemon/translated/{pokemonName}` | Returns Pokémon info with translated description |

### Example requests

```bash
curl http://localhost:8080/pokemon/mewtwo

curl http://localhost:8080/pokemon/translated/mewtwo
```

### Example response

```json
{
  "name": "mewtwo",
  "description": "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
  "habitat": "rare",
  "isLegendary": true
}
```

---

## Error responses

| Status | Cause |
|---|---|
| `400 Bad Request` | Pokémon name is blank |
| `404 Not Found` | Pokémon does not exist |
| `502 Bad Gateway` | Upstream PokéAPI returned an error |

---

## Run independently (without Docker)

### 1. Build the project

```bash
mvn package -DskipTests
```

### 2. Start the application

```bash
java -jar target/pokemon-codex-adapter-0.0.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`.

### 3. Stop the application

Press `Ctrl + C` in the terminal where the application is running.

---

## Bruno collection

The file `pokemon adapter endpoints.zip` contains a Bruno collection with pre-configured requests for all available endpoints.

### 1. Unzip the collection

```bash
unzip "pokemon adapter endpoints.zip" -d "pokemon adapter endpoints"
```

### 2. Open the collection in Bruno

1. Open **Bruno**
2. Click **Open Collection**
3. Select the unzipped `pokemon adapter endpoints` folder
4. All endpoints will appear in the sidebar ready to use

> Make sure the application is running on `http://localhost:8080` before sending requests.

---

## Road to production

### Swagger / OpenAPI documentation
Integrate [SpringDoc OpenAPI](https://springdoc.org/) (`springdoc-openapi-starter-webmvc-ui`) to auto-generate an interactive API documentation UI at `/swagger-ui.html`. This allows the endpoints, request parameters, and response shapes to be explored and shared without reading the source code, making onboarding and integration by consumers significantly faster.

### Caching
Add a caching layer (e.g. Spring's `@Cacheable` backed by **Caffeine** for in-memory or **Redis** for distributed caching) on both `getPokemonInfo` and `getTranslatedPokemonInfo`, keyed by Pokémon name. This avoids redundant calls to the upstream APIs for the same Pokémon and, more critically, prevents hitting the Fun Translations API rate limit (`429 Too Many Requests`) which allows only a small number of requests per hour on the free tier.

### Rate limiter (token bucket)
Implement a token bucket rate limiter (e.g. via [Bucket4j](https://github.com/bucket4j/bucket4j)) at the application level to cap the number of requests served per time window. This protects both the upstream translation API from being overwhelmed and the application itself from abuse. The token bucket model is well suited here because it allows short bursts while enforcing a sustained average throughput, which maps naturally to the free-tier constraints of the Fun Translations API.
