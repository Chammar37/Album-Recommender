# Album Recommender - Application Specification

## Overview

**Application Name:** Album Recommender
**Purpose:** A web application that provides intelligent album recommendations using Spotify's API combined with customizable audio feature parameters.
**Inspiration:** Based on the same.energy application concept
**Status:** In Active Development

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Technology Stack](#technology-stack)
3. [Backend Specification](#backend-specification)
4. [Frontend Specification](#frontend-specification)
5. [API Reference](#api-reference)
6. [Data Models](#data-models)
7. [External Integrations](#external-integrations)
8. [Configuration](#configuration)
9. [Build & Deployment](#build--deployment)
10. [Security Considerations](#security-considerations)
11. [Future Enhancements](#future-enhancements)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Svelte + Vite Frontend                      │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │    │
│  │  │ App.svelte  │  │Album.svelte │  │SimilarAlbums    │  │    │
│  │  │ (Main)      │  │ (Card)      │  │.svelte          │  │    │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘  │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/REST (JSON)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       BACKEND LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Spring Boot 3.0.4 (Java 17)                 │    │
│  │  ┌─────────────────┐        ┌─────────────────────┐     │    │
│  │  │ AlbumController │ ◄────► │   SpotifyService    │     │    │
│  │  │ (REST API)      │        │   (Business Logic)  │     │    │
│  │  └─────────────────┘        └─────────────────────┘     │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS/OAuth2
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL SERVICES                             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   Spotify Web API                        │    │
│  │  • Authentication (OAuth2 Client Credentials)            │    │
│  │  • Search API                                            │    │
│  │  • Recommendations API                                   │    │
│  │  • Artists API                                           │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Technology Stack

### Backend
| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 3.0.4 |
| Language | Java | 17 |
| Build Tool | Gradle | - |
| HTTP Client | RestTemplate | - |
| JSON Processing | Jackson Databind | - |
| Template Engine | Thymeleaf | - |
| Spotify SDK | Spotify Web API Java | 6.5.4 |

### Frontend
| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Svelte | 3.55.1 |
| Build Tool | Vite | 4.2.0 |
| Module System | ES Modules | - |
| HTTP Client | Fetch API | Native |

---

## Backend Specification

### Project Structure

```
src/main/java/com/example/albumrecomendar/
├── AlbumRecomendarApplication.java    # Main Spring Boot application
├── controller/
│   └── AlbumController.java           # REST API endpoints
├── service/
│   └── SpotifyService.java            # Spotify API integration
├── model/
│   ├── Album.java                     # Album data model
│   ├── Artist.java                    # Artist data model
│   ├── Image.java                     # Image URL model
│   ├── Albums.java                    # Album list wrapper
│   ├── SpotifyTrack.java              # Track data model
│   ├── SpotifySeeds.java              # Recommendation seeds model
│   ├── SpotifyTokenResponse.java      # OAuth token response model
│   ├── SpotifyRecommendationsResponse.java  # Recommendations response
│   └── AlbumDeserializer.java         # Custom JSON deserializer
└── config/
    ├── SpotifyConfig.java             # RestTemplate bean config
    └── SpotifyApiConfig.java          # API configuration
```

### Services

#### SpotifyService

The core service handling all Spotify API interactions.

**Responsibilities:**
- OAuth2 token management with automatic refresh
- Album search functionality
- Artist genre retrieval
- Recommendation generation

**Token Management:**
- Implements token caching with expiration tracking
- Auto-refreshes tokens 5 minutes before expiry
- Uses `Instant` for time-based validation

---

## Frontend Specification

### Project Structure

```
albumrecomendar-frontend/
├── src/
│   ├── App.svelte              # Main application component
│   ├── Album.svelte            # Album card component
│   ├── SimilarAlbums.svelte    # Similar albums grid
│   ├── main.js                 # Application entry point
│   ├── app.css                 # Global styles
│   ├── lib/                    # Reusable components
│   │   ├── Button.svelte
│   │   └── Counter.svelte
│   ├── helpers/                # Utility scripts
│   └── assets/                 # Static assets
├── index.html                  # HTML shell
├── package.json                # Dependencies
├── vite.config.js              # Vite configuration
└── svelte.config.js            # Svelte configuration
```

### Component Hierarchy

```
App.svelte (Root)
├── Album.svelte (Card) [multiple]
└── SimilarAlbums.svelte
    └── Album.svelte (Card) [multiple]
```

### Component Details

#### App.svelte
- **State:** `albums` (array of album objects)
- **Lifecycle:** Fetches recommendations on mount
- **Events:** `handleAlbumClick`, `handleKeyDown`

#### Album.svelte
- **Props:** `id`, `name`, `artist`, `images`
- **Events:** Dispatches `albumselect` custom event
- **Behavior:** Clickable card triggering search

#### SimilarAlbums.svelte
- **Props:** `similarAlbums` (array)
- **Behavior:** Renders grid of Album components

---

## API Reference

### Base URL
```
Development: http://localhost:8080
```

### Endpoints

#### GET /search
Search for albums by query string.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| query | string | Yes | Album/song search query |

**Response:** `List<Album>`

**Example:**
```bash
GET /search?query=Dark%20Side%20of%20the%20Moon
```

---

#### GET /genre
Retrieve genres for a specific artist.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| query | string | Yes | Spotify artist ID |

**Response:** `List<String>`

**Example:**
```bash
GET /genre?query=2ANVost0y2y52ema1E9xAZ
```

---

#### GET /recommendations
Get album recommendations based on seed parameters.

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| seedArtist | string | Yes | Spotify artist ID |
| seedGenres | string | Yes | Genre(s) for seeding |
| seedTracks | string | Yes | Spotify track ID |
| targetEnergy | string | No | Target energy (0.0-1.0) |
| targetDanceability | string | No | Target danceability (0.0-1.0) |
| targetValence | string | No | Target valence/positivity (0.0-1.0) |

**Response:** `List<Album>`

**Example:**
```bash
GET /recommendations?seedArtist=2ANVost0y2y52ema1E9xAZ&seedGenres=hip-hop&seedTracks=0c6xIDDpzE81m2q797ordA&targetEnergy=0.7&targetDanceability=0.7&targetValence=0.1
```

---

#### GET /test
Test endpoint returning a Thymeleaf template.

**Response:** HTML template

---

## Data Models

### Album
```java
{
  "id": "string",           // Spotify album ID
  "title": "string",        // Album name
  "artists": [Artist],      // List of artists
  "images": [Image]         // Cover art images
}
```

### Artist
```java
{
  "id": "string",           // Spotify artist ID
  "name": "string",         // Artist name
  "genres": ["string"]      // Associated genres
}
```

### SpotifyTrack
```java
{
  "id": "string",           // Track ID
  "name": "string",         // Track name
  "artists": [Artist],      // Track artists
  "album": Album            // Parent album
}
```

### Image
```java
{
  "url": "string"           // Image URL
}
```

### SpotifyRecommendationsResponse
```java
{
  "tracks": [SpotifyTrack], // Recommended tracks
  "seeds": [SpotifySeeds]   // Seeds used for recommendations
}
```

---

## External Integrations

### Spotify Web API

**Authentication:** OAuth2 Client Credentials Flow

**Endpoints Used:**

| Endpoint | Purpose |
|----------|---------|
| `https://accounts.spotify.com/api/token` | Token generation |
| `https://api.spotify.com/v1/search` | Album search |
| `https://api.spotify.com/v1/recommendations` | Album recommendations |
| `https://api.spotify.com/v1/artists` | Artist information (genres) |

**Recommendation Audio Features:**

| Feature | Range | Description |
|---------|-------|-------------|
| Energy | 0.0-1.0 | Intensity and activity level |
| Danceability | 0.0-1.0 | Rhythm suitability for dancing |
| Valence | 0.0-1.0 | Musical positivity/happiness |

---

## Configuration

### Backend (application.properties)

```properties
# Spotify API Configuration
spotify.api.client.id=${SPOTIFY_CLIENT_ID}
spotify.api.client.secret=${SPOTIFY_CLIENT_SECRET}
spotify.api.token.url=https://accounts.spotify.com/api/token
spotify.api.search.url=https://api.spotify.com/v1/search
spotify.api.recommendations.url=https://api.spotify.com/v1/recommendations
spotify.api.artists.url=https://api.spotify.com/v1/artists

# Logging
logging.level.root=INFO

# Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false
```

### Frontend (vite.config.js)

```javascript
import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

export default defineConfig({
  plugins: [svelte()]
})
```

---

## Build & Deployment

### Backend

**Build:**
```bash
./gradlew build
```

**Run:**
```bash
./gradlew bootRun
```

**Artifact:** `com.example:albumrecomendar:0.0.1-SNAPSHOT`

### Frontend

**Install Dependencies:**
```bash
cd albumrecomendar-frontend
npm install
```

**Development:**
```bash
npm run dev
```

**Production Build:**
```bash
npm run build
```

**Preview:**
```bash
npm run preview
```

---

## Security Considerations

### Current Implementation

| Aspect | Status | Notes |
|--------|--------|-------|
| API Credentials | ⚠️ Risk | Exposed in properties file |
| CORS | ⚠️ Open | All origins allowed (@CrossOrigin) |
| User Auth | ❌ None | No user authentication |
| Input Validation | ⚠️ Basic | Limited validation on inputs |
| HTTPS | ❌ Dev Only | HTTP in development |

### Recommended Improvements

1. **Environment Variables:** Move Spotify credentials to environment variables
2. **CORS Restrictions:** Limit allowed origins to specific domains
3. **Input Sanitization:** Add comprehensive input validation
4. **Rate Limiting:** Implement API rate limiting
5. **HTTPS:** Enforce HTTPS in production

---

## Future Enhancements

### Planned Features

1. **User Interface**
   - Search input field for album/artist queries
   - Adjustable sliders for audio features (energy, danceability, valence)
   - User preferences and history

2. **Backend Enhancements**
   - Database integration for user data persistence
   - Recommendation history tracking
   - Custom recommendation algorithms (beyond Spotify API)

3. **Model Training**
   - Machine learning model for personalized recommendations
   - User preference learning

4. **Infrastructure**
   - User authentication/authorization
   - API documentation (OpenAPI/Swagger)
   - Comprehensive test coverage
   - CI/CD pipeline

### Technical Debt

- Remove commented-out code throughout codebase
- Implement proper error handling in frontend
- Make frontend parameters configurable (currently hardcoded)
- Add pagination for search results
- Improve accessibility features

---

## File Inventory

### Backend Files
| File | Purpose |
|------|---------|
| `AlbumRecomendarApplication.java` | Spring Boot entry point |
| `AlbumController.java` | REST API endpoints |
| `SpotifyService.java` | Spotify API integration |
| `Album.java` | Album data model |
| `Artist.java` | Artist data model |
| `SpotifyTrack.java` | Track data model |
| `SpotifyConfig.java` | Bean configuration |
| `application.properties` | Application settings |

### Frontend Files
| File | Purpose |
|------|---------|
| `main.js` | Application entry point |
| `App.svelte` | Main application component |
| `Album.svelte` | Album card component |
| `SimilarAlbums.svelte` | Album grid component |
| `app.css` | Global styles |
| `vite.config.js` | Build configuration |

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 0.0.1-SNAPSHOT | Current | Initial development version |

---

*Document generated: November 2025*
*Last updated: Based on current codebase analysis*
