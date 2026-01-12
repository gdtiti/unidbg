# Project Structure

```
src/main/java/com/anjia/unidbgserver/
├── UnidbgServerApplication.java    # Spring Boot entry point
├── config/                         # Configuration classes
│   ├── UnidbgProperties.java       # Unidbg emulator settings
│   ├── FQApiProperties.java        # API endpoint configuration
│   ├── FQDownloadProperties.java   # Download/retry settings
│   └── JacksonConfig.java          # JSON serialization config
├── dto/                            # Data Transfer Objects
│   ├── FQNovelRequest.java         # API request models
│   ├── FQNovelResponse.java        # Generic response wrapper
│   ├── FQNovelBookInfo.java        # Book metadata
│   ├── FQNovelChapterInfo.java     # Chapter content
│   └── ...                         # Other request/response DTOs
├── service/                        # Business logic
│   ├── FQNovelService.java         # Main novel content service
│   ├── FQEncryptService.java       # Signature generation wrapper
│   ├── FQEncryptServiceWorker.java # Async signature worker
│   ├── FQSearchService.java        # Search functionality
│   ├── FQRegisterKeyService.java   # Decryption key management
│   ├── DeviceManagementService.java # Device config management
│   └── ...                         # Other services
├── unidbg/
│   └── IdleFQ.java                 # Android emulator + native lib calls
├── utils/                          # Utility classes
│   ├── FQApiUtils.java             # API URL/param builders
│   ├── TempFileUtils.java          # Temp file management
│   └── ConsoleNoiseFilter.java     # Log filtering
└── web/                            # REST Controllers
    ├── FQNovelController.java      # /api/fqnovel/* endpoints
    ├── FQSearchController.java     # Search endpoints
    ├── FQEncryptController.java    # Signature endpoints
    └── DeviceManagementController.java

src/main/resources/
├── application.yml                 # Main configuration
├── logback-spring.xml              # Logging configuration
└── com/dragon/read/oversea/gp/    # APK and native libraries
    ├── apk/base.apk
    ├── lib/*.so
    └── other/*.bin
```

## Architecture Patterns

- **Layered Architecture**: Controller → Service → Unidbg/External APIs
- **Async Processing**: Services return `CompletableFuture<T>` for non-blocking operations
- **Configuration Properties**: Use `@ConfigurationProperties` with prefix binding
- **DTOs**: Lombok `@Data` for request/response objects
- **Response Wrapper**: `FQNovelResponse<T>` with code, message, data pattern
