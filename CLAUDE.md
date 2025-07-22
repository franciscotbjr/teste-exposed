# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ IMPORTANT: Always Check Project Specifications

**Before starting any work on this project, ALWAYS examine the `specs/` folder first.** This folder contains:

- Project phases and development requirements
- Implementation specifications for each feature
- Current development status and completed work
- Architectural decisions and constraints
- User interface requirements and design guidelines

**Key specification files to review:**
- `specs/04-instrucoes-fase-04-jfx.md` - Current phase requirements
- `specs/FASE-04-IMPLEMENTACAO-EM-ANDAMENTO.md` - Implementation status
- `specs/00-instrucoes-gerais-interface-jfx.md` - General interface guidelines
- Other phase files (01, 02, 03) for historical context

**Essential workflow:**
1. **First**: Read relevant specification files in `specs/`
2. **Then**: Reference this CLAUDE.md for technical implementation details
3. **Always**: Align your work with the specifications and current phase requirements

## Project Overview

HexaSilith Chat is a Kotlin-based AI chat application using DeepSeek API integration. It features both CLI and JavaFX GUI interfaces, with sophisticated conversation management and summarization capabilities.

## Essential Commands

### Running the Application
```bash
# Default GUI mode
./gradlew run

# CLI mode  
./gradlew run --args="cli"

# Explicit GUI mode
./gradlew run --args="gui"
```

### Development Commands
```bash
# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "*ConversationServiceTest*"
./gradlew test --tests "*ConversationSummarizationRepositoryTest*"

# Build application
./gradlew build

# Create shadow JAR
./gradlew shadowJar
```

### Database Operations
- Database migrations run automatically via Flyway on application startup
- Database file location: `./data/chat.db` (configurable in `application.conf`)
- Migration scripts in `src/main/resources/db/migration/`

## Architecture Overview

### Layer Structure
```
Controller Layer → Service Layer → Repository Layer → Database
     ↓                ↓              ↓
Presentation ← Business Logic ← Data Access
```

### Key Components

**Configuration**
- `AppConfig`: API keys and application settings via Typesafe Config
- `DatabaseConfig`: SQLite + HikariCP + Flyway automatic setup

**Services**
- `ConversationService`: Core business logic, conversation and summarization management
- `AIService`: DeepSeek API integration with Ktor HTTP client

**Repositories** 
- Follow repository pattern with Exposed ORM
- All operations are transaction-scoped
- Key repos: `ConversationRepository`, `MessageRepository`, `ConversationSummarizationRepository`

**Presentation**
- `IntegratedMainController`: Main JavaFX interface controller
- `MarkdownView`: Custom JavaFX component for native markdown rendering
- Modal controllers for summarization workflows

### Dual Interface Architecture
- **CLI Mode**: `ChatController` handles console commands (`/new`, `/list`, `/load <id>`, `/exit`)
- **GUI Mode**: JavaFX with FXML layouts and CSS styling
- Entry point selection in `Main.kt` based on command line arguments

## Development Guidelines

### Database Schema Management
- **Never modify existing migrations** - always create new V{n}__ files
- Schema managed by Flyway + Exposed DSL table definitions
- Current tables: `roles`, `conversations`, `messages`, `api_raw_responses`, `conversations_summarizations`

### Configuration Requirements
Create `src/main/resources/application.conf`:
```hocon
api {
  key = "your-deepseek-api-key"
}

database {
  path = "./data/chat.db"
}
```

### Testing Approach
- Repository tests use real SQLite database with cleanup
- Service tests use mocked repositories
- JavaFX tests extend `JavaFXTestBase` for Platform initialization
- Mock API responses in `AIServiceTest`

### Async Patterns
- Use Kotlin coroutines for API calls and heavy operations
- GUI updates must run on JavaFX Application Thread
- `kotlinx-coroutines-javafx` integration for UI thread dispatch

### Code Organization Patterns
- Manual dependency injection (no DI framework)
- Repository pattern for data access
- Service layer for business logic
- Controller layer handles user interaction
- Data converter utilities for model transformation

## Key Technical Details

### API Integration
- DeepSeek API with extended timeouts (10 minutes)
- All raw responses logged to `api_raw_responses` table
- Token counting and 128k limit management
- Portuguese-optimized summarization prompts

### JavaFX Specifics
- FXML layouts in `src/main/resources/fxml/`
- CSS styling in `src/main/resources/css/`
- Custom `MarkdownView` replaces WebView for better performance
- Modal dialogs for summarization workflows

### Database Specifics
- SQLite with Exposed ORM using DSL syntax
- HikariCP connection pooling (pool size: 1 for SQLite)
- Flyway migrations for schema versioning
- Soft delete pattern for summarizations (`is_active` field)

## Current Development Phase

**Phase 4: Conversation Summarization (In Progress)**
- ✅ GUI components and modals implemented
- ✅ Real DeepSeek API integration
- ✅ Token management and alerts
- ✅ Database persistence layer
- 🔄 Performance optimizations ongoing

## Technology Stack
- **Language**: Kotlin 2.1.21 (Java 21 toolchain)
- **Build**: Gradle with Kotlin DSL
- **Database**: SQLite + Exposed ORM + Flyway
- **HTTP**: Ktor Client with CIO engine
- **GUI**: JavaFX 21 with FXML
- **Async**: Kotlin Coroutines + kotlinx-coroutines-javafx
- **Testing**: JUnit 5 + Mockito Kotlin

## Common Patterns to Follow

When adding new features:
1. **FIRST**: Review `specs/` folder for requirements and constraints
2. Create database migration if schema changes needed
3. Add repository methods for data access
4. Implement business logic in service layer
5. Create/update controllers for user interaction
6. Add comprehensive tests for each layer
7. Follow existing error handling patterns
8. Use coroutines for async operations
9. Maintain separation between CLI and GUI code paths
10. **ALWAYS**: Validate implementation against specifications