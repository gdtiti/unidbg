# Technology Stack

## Build System
- **Maven** (pom.xml)
- Java 8 (1.8)
- Output: `target/fqnovel.jar`

## Framework & Libraries
- **Spring Boot 2.6.3** - Web framework
- **Unidbg 0.9.8** - Android emulation for native library execution
  - unidbg-api, unidbg-android, unidbg-dynarmic, unidbg-unicorn2
- **Lombok** - Boilerplate reduction (@Data, @Slf4j)
- **Jackson** - JSON serialization
- **Apache Commons** - Lang3, IO, Codec utilities
- **StreamEx** - Enhanced stream operations

## Runtime
- JDK 8 (JRE)
- Docker support (eclipse-temurin:8-jre-jammy)
- Default port: 9999

## Common Commands

```bash
# Build (skip tests)
mvn -DskipTests package

# Run locally
java -jar target/fqnovel.jar

# Docker run
docker run -d --name fqnovel --restart=always -p 9999:9999 gxmandppx/unidbg-fq:latest
```

## Configuration
- Primary config: `src/main/resources/application.yml`
- External config: Place `application.yml` in working directory to override
- Environment variables: `SERVER_PORT`, `SERVER_ADDRESS`

## Key Configuration Properties
- `application.unidbg.*` - Emulator settings (dynarmic, verbose, apk-path)
- `fq.api.*` - API base URL, device info, user-agent
- `fq.download.*` - Retry settings, request intervals
