# Refactoring Guide: Modular Architecture & Decoupling Strategy

## 1. Findings Summary
The current codebase follows a "Package by Feature" structure (`movie`, `stream`, `helloworld`), which is a good starting point. However, the implementation within these packages exhibits tight coupling:
- **Direct Concrete Dependencies:** `MovieGrpcService` directly depends on the concrete class `MovieDataService`.
- **No Abstraction Layer:** There is no interface defining the contract between the transport layer (gRPC) and the business logic layer.
- **Missing Feature Toggles:** There is no infrastructure to safely introduce new logic or swap implementations behind a flag.
- **Mixed Responsibilities:** `MovieDataService` handles both complex JSON parsing logic and database interactions.

## 2. Risks Identified
- **Refactoring Friction:** Changing the underlying data storage (e.g., SQL to NoSQL) or logic requires modifying the consumer (`MovieGrpcService`), leading to ripple effects.
- **Testing Difficulty:** Unit testing `MovieGrpcService` requires mocking the concrete `MovieDataService` class rather than a stable interface, which can be brittle.
- **Parallel Development Conflicts:** Without interfaces and toggles, two developers working on "Movie" logic will likely modify the same files (`MovieDataService`), causing merge conflicts.
- **Deployment Risk:** New features cannot be deployed in a "dormant" state; any code change is live immediately.

## 3. Refactoring Target: Movie Feature
We will refactor the `movie` package to introduce a strict separation of concerns, interfaces, and a toggle-based strategy pattern.

## 4. Before / After File & Folder Structure

### Before
```text
src/main/java/com/mdb/media_data_gateway_service/movie/
├── MovieDataService.java (Concrete Service)
├── MovieGrpcService.java (gRPC Controller)
├── TorrentEntity.java    (DB Entity)
└── TorrentRepository.java(JPA Repository)
```

### After
```text
src/main/java/com/mdb/media_data_gateway_service/movie/
├── api/
│   └── MovieService.java       (Stable Interface)
├── internal/
│   ├── config/
│   │   └── MovieConfig.java    (Strategy/Toggle Configuration)
│   ├── impl/
│   │   ├── SqlMovieService.java (Original Implementation)
│   │   └── NoOpMovieService.java (Fallback/Stub Implementation)
│   └── repo/
│       ├── TorrentEntity.java
│       └── TorrentRepository.java
└── MovieGrpcService.java       (Depends ONLY on api.MovieService)
```

## 5. Before / After Key Code Examples

### Before: Tight Coupling
**MovieGrpcService.java**
```java
@GrpcService
@RequiredArgsConstructor
public class MovieGrpcService extends ... {
    // Problem: Depends on concrete implementation
    private final MovieDataService movieDataService; 

    @Override
    public void getMovie(...) {
        // Problem: If logic changes, this might break
        movieDataService.getMovieJson(...); 
    }
}
```

### After: Strategy Pattern & Interfaces

**1. The Interface (API)**
```java
// src/main/java/com/mdb/media_data_gateway_service/movie/api/MovieService.java
public interface MovieService {
    int saveMoviesFromJson(String jsonContent);
    String getMovieJson(String imdbId);
}
```

**2. The Concrete Implementation (Internal)**
```java
// src/main/java/com/mdb/media_data_gateway_service/movie/internal/impl/SqlMovieService.java
@Service("sqlMovieService") // Named bean
@RequiredArgsConstructor
public class SqlMovieService implements MovieService {
    private final TorrentRepository torrentRepository;
    // ... implementation details ...
}
```

**3. The Configuration (Strategy Strategy)**
```java
// src/main/java/com/mdb/media_data_gateway_service/movie/internal/config/MovieConfig.java
@Configuration
public class MovieConfig {

    @Value("${feature.toggles.movie-service:sql}")
    private String movieServiceMode;

    @Bean
    @Primary
    public MovieService movieService(
            @Qualifier("sqlMovieService") MovieService sqlService,
            @Qualifier("noOpMovieService") MovieService noOpService) {
        
        // Strategy Pattern: Select implementation based on config
        return switch (movieServiceMode) {
            case "sql" -> sqlService;
            case "noop" -> noOpService;
            default -> throw new IllegalStateException("Unknown movie service mode: " + movieServiceMode);
        };
    }
}
```

**4. The Decoupled Consumer**
```java
// src/main/java/com/mdb/media_data_gateway_service/movie/MovieGrpcService.java
@GrpcService
@RequiredArgsConstructor
public class MovieGrpcService extends ... {
    // Benefit: Depends on stable interface
    private final MovieService movieService; 
}
```

## 6. Refactoring Rationale
1.  **Isolation:** `MovieGrpcService` no longer knows *how* data is stored. We can swap SQL for ElasticSearch without touching the gRPC layer.
2.  **Safety:** We can deploy new implementations (e.g., `NewOptimizedMovieService`) alongside the old one and switch them using an environment variable (`feature.toggles.movie-service`).
3.  **Testability:** We can easily create a `MockMovieService` for testing the gRPC layer without spinning up a database context.

## 7. Recommendations for Other Similar Modules
- **`stream`**: If logic grows beyond a simple controller, introduce `StreamService` interface and `StreamProvider` implementations.
- **`helloworld`**: Keep as is (it's a sample), but ensure it doesn't leak into core business logic.

## 8. Golden Path for Future Features
For any **NEW** feature (e.g., "Reviews"), follow this structure from Day 1.

### Proposed Structure: `review`
```text
src/main/java/com/mdb/media_data_gateway_service/review/
├── api/
│   ├── ReviewService.java          (Interface)
│   └── model/                      (DTOs)
├── internal/
│   ├── DefaultReviewService.java   (Implementation)
│   ├── ReviewToggleConfig.java     (Strategy Config)
│   └── repository/                 (Data Access)
└── ReviewGrpcService.java          (Consumer)
```

### Golden Path Implementation

**1. Interface (`api/ReviewService.java`)**
```java
public interface ReviewService {
    void addReview(String movieId, String content);
}
```

**2. Strategy Configuration (`internal/ReviewToggleConfig.java`)**
```java
@Configuration
public class ReviewToggleConfig {
    @Bean
    @Primary
    public ReviewService reviewService(
            @Value("${features.review.enabled:false}") boolean isEnabled,
            ObjectProvider<DefaultReviewService> defaultService) {
        
        if (isEnabled) {
            return defaultService.getIfAvailable();
        } else {
            return (movieId, content) -> log.info("Reviews feature disabled. Ignoring.");
        }
    }
}
```

**3. Integration Test with Toggle (`test/.../ReviewFeatureTest.java`)**
```java
@SpringBootTest(properties = "features.review.enabled=true")
class ReviewFeatureEnabledTest {
    @Autowired ReviewService reviewService;
    // Test that logic executes
}

@SpringBootTest(properties = "features.review.enabled=false")
class ReviewFeatureDisabledTest {
    @Autowired ReviewService reviewService;
    // Test that logic is skipped/safe
}
```
