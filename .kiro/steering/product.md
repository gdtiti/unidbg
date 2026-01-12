# Product Overview

FQNovel Unidbg Server is a Spring Boot service that provides API signature generation and novel content retrieval for the FQNovel (番茄小说) application.

## Core Functionality

- **API Signature Generation**: Uses unidbg to emulate Android native libraries (`libmetasec_ml.so`) for generating request signatures (X-Argus, X-Khronos, etc.)
- **Novel Content API**: Provides REST endpoints for fetching book info, chapter content, batch chapters, and search functionality
- **Content Decryption**: Handles encrypted chapter content decryption using registered keys
- **Device Management**: Manages virtual device configurations for API requests with automatic rotation on rate limiting

## Key Features

- Async request processing with CompletableFuture
- Automatic retry with exponential backoff for upstream API failures
- Device rotation on ILLEGAL_ACCESS errors
- GZIP response handling
- Chapter content prefetching and caching
