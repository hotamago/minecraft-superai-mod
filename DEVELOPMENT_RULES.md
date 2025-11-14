# SuperAI Minecraft Bot - Development Rules & Guidelines

## 📋 Project Overview
This project creates an intelligent AI bot system for Minecraft that allows external Python agents to interact with the game world through a gRPC API. The system enables AI agents to act as real players rather than simple NPCs, with intelligent environment recognition capabilities.

## 🏗️ Architecture Principles

### 1. Modular Design
- **Separation of Concerns**: Each module has a single, well-defined responsibility
- **Dependency Injection**: Use constructor injection for dependencies
- **Interface Segregation**: Prefer interfaces over concrete implementations for better testability

### 2. Clean Code Standards
- **SOLID Principles**: Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **DRY (Don't Repeat Yourself)**: Eliminate code duplication through proper abstraction
- **KISS (Keep It Simple, Stupid)**: Prefer simple solutions over complex ones
- **YAGNI (You Aren't Gonna Need It)**: Don't implement features until they're actually needed

### 3. Naming Conventions
- **Classes**: PascalCase (e.g., `EnvironmentScanner`, `PlayerController`)
- **Methods**: camelCase (e.g., `scanEnvironment()`, `executeAction()`)
- **Variables**: camelCase (e.g., `playerPosition`, `blockState`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_CONNECTIONS`, `DEFAULT_TIMEOUT`)
- **Packages**: lowercase with dots (e.g., `com.supermc.ai.grpc`, `com.supermc.ai.environment`)

## 📁 Project Structure

```
src/main/java/com/supermc/ai/
├── grpc/                    # gRPC service implementations
│   ├── service/            # Core gRPC services
│   ├── proto/              # Generated protobuf classes
│   └── interceptor/        # gRPC interceptors
├── environment/            # Environment perception system
│   ├── scanner/            # World scanning logic
│   ├── analyzer/           # Environment analysis
│   └── cache/              # Environment data caching
├── player/                 # Player control system
│   ├── controller/         # Player action control
│   ├── state/              # Player state management
│   └── inventory/          # Inventory management
├── config/                 # Configuration management
├── common/                 # Shared utilities and constants
└── SuperAIMod.java        # Main mod class
```

## 🔧 Development Rules

### 1. Code Quality Standards

#### Java Best Practices
- Use Java 21 features appropriately (records, pattern matching, etc.)
- Prefer immutable objects where possible
- Use Optional for nullable return values
- Avoid null checks with proper null-safety patterns

#### Documentation
- All public APIs must have JavaDoc comments
- Complex business logic must have inline comments
- Method contracts should be clearly documented (preconditions, postconditions, exceptions)

#### Error Handling
- Use custom exceptions for business logic errors
- Log errors with appropriate levels (ERROR, WARN, INFO, DEBUG)
- Never swallow exceptions without proper handling
- Use try-with-resources for resource management

### 2. gRPC API Design

#### Service Organization
- Each service handles a specific domain (Environment, Player, Inventory)
- Services should be stateless where possible
- Use streaming for real-time data (environment updates, player actions)

#### Message Design
- Keep messages small and focused
- Use enums for fixed sets of values
- Include version fields for API evolution
- Validate all inputs on both client and server

#### Performance Considerations
- Implement connection pooling
- Use compression for large messages
- Implement proper timeout handling
- Cache frequently accessed data

### 3. Threading & Concurrency

#### Minecraft Thread Safety
- All Minecraft API calls must be on the main thread
- Use `Minecraft.getInstance().execute()` for main thread operations
- Implement proper synchronization for shared state
- Avoid blocking operations on main thread

#### gRPC Threading
- Use appropriate thread pools for gRPC services
- Implement proper cancellation handling
- Use reactive patterns for async operations

### 4. Testing Strategy

#### Unit Testing
- Test all business logic classes
- Mock external dependencies (Minecraft API, network calls)
- Use JUnit 5 with AssertJ for assertions
- Aim for >80% code coverage

#### Integration Testing
- Test gRPC service interactions
- Test Minecraft mod integration
- Use test containers for isolated testing

### 5. Configuration Management

#### Config Categories
- Server settings (host, port, timeouts)
- Environment scanning parameters (scan radius, update frequency)
- Player control settings (action delays, safety checks)
- Logging configuration

#### Config Validation
- Validate all configuration values on startup
- Provide sensible defaults
- Support hot-reloading where possible
- Document all configuration options

## 🚀 Development Workflow

### 1. Feature Development Process
1. Create issue/feature request with detailed requirements
2. Design API contracts and interfaces
3. Implement core logic with tests
4. Add configuration support
5. Update documentation
6. Code review and testing

### 2. Code Review Checklist
- [ ] Code follows established patterns and conventions
- [ ] Unit tests are included and pass
- [ ] Documentation is updated
- [ ] No security vulnerabilities introduced
- [ ] Performance impact assessed
- [ ] Thread safety verified

### 3. Version Control
- Use feature branches for development
- Write clear, descriptive commit messages
- Squash commits before merging
- Tag releases with semantic versioning

## 🔒 Security Considerations

### 1. gRPC Security
- Implement authentication for gRPC services
- Use TLS encryption in production
- Validate all input data thoroughly
- Implement rate limiting

### 2. Minecraft Integration
- Respect Minecraft's terms of service
- Implement proper permission checks
- Avoid exploits or unfair advantages
- Log all player actions for audit trails

## 📊 Performance Guidelines

### 1. Memory Management
- Implement object pooling for frequently created objects
- Use weak references for caches
- Monitor memory usage and implement cleanup
- Avoid memory leaks in long-running operations

### 2. CPU Optimization
- Profile performance-critical code
- Use efficient data structures
- Implement caching strategies
- Batch operations where possible

### 3. Network Efficiency
- Compress gRPC messages
- Implement connection pooling
- Use streaming for large data transfers
- Implement proper timeout and retry logic

## 🧪 Quality Assurance

### 1. Automated Testing
- Run unit tests on every build
- Integration tests for critical paths
- Performance benchmarks
- Memory leak detection

### 2. Manual Testing
- Test in various Minecraft environments
- Load testing with multiple AI agents
- Stress testing edge cases
- Cross-version compatibility testing

## 📚 Documentation

### 1. Code Documentation
- Maintain up-to-date README.md
- API documentation for external consumers
- Internal wiki for development knowledge
- Code comments for complex algorithms

### 2. User Documentation
- Installation and setup guides
- Configuration reference
- Troubleshooting guides
- API usage examples

## 🔄 Maintenance & Evolution

### 1. Dependency Management
- Keep dependencies updated regularly
- Use semantic versioning for releases
- Document breaking changes
- Maintain backward compatibility where possible

### 2. Code Evolution
- Refactor legacy code during feature development
- Maintain coding standards across the codebase
- Regular code reviews and cleanup
- Archive deprecated features properly

---

## 📝 Implementation Checklist

Before starting development:
- [ ] Project structure created
- [ ] Core dependencies added (gRPC, testing frameworks)
- [ ] Basic mod setup completed
- [ ] Configuration system implemented
- [ ] Logging framework configured
- [ ] Basic gRPC service skeleton created
- [ ] Unit test framework set up
- [ ] CI/CD pipeline configured

This document will be updated as the project evolves and new patterns emerge.
