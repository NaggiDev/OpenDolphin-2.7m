# OpenDolphin Server Migration to Spring Boot

## Overview
This document outlines the migration plan from Jakarta EE to Spring Boot for the OpenDolphin server component. The goal is to modernize the framework while maintaining high compatibility with the existing web application architecture.

**Current State**: Jakarta EE 7, JAX-RS, EJB, CDI, JPA  
**Target State**: Spring Boot 3.x, Spring MVC, Spring Services, Spring Data JPA

## Migration Phases

### Phase 1: Project Setup and Foundation ✅ COMPLETED
- [x] Create new `server-spring` Maven module
- [x] Set up Spring Boot 3.x with Java 17
- [x] Configure basic dependencies (web, data-jpa, security, postgresql)
- [x] Set up project structure mirroring current server module
- [x] Configure application.yml for database and basic settings
- [x] Set up Spring Security for authentication/authorization
- [x] Create initial Spring Boot main application class

### Phase 2: Core Migration
#### Service Layer Migration
- [x] Migrate PatientServiceBean to PatientService (@Service)
- [x] Migrate KarteServiceBean to KarteService
- [x] Migrate PVTServiceBean to PVTService
- [ ] Migrate UserServiceBean to UserService
- [ ] Migrate StampServiceBean to StampService
- [ ] Migrate SystemServiceBean to SystemService
- [x] Migrate ChartEventServiceBean to ChartEventService
- [ ] Migrate ScheduleServiceBean to ScheduleService
- [ ] Migrate LetterServiceBean to LetterService
- [ ] Migrate MmlServiceBean to MmlService
- [ ] Migrate NLabServiceBean to NLabService
- [ ] Migrate VitalServiceBean to VitalService
- [ ] Migrate AppoServiceBean to AppoService
- [x] Replace @Inject with @Autowired in all services
- [x] Update service method signatures for Spring compatibility

#### REST Layer Migration
- [x] Migrate PatientResource to PatientController (@RestController)
- [x] Migrate KarteResource to KarteController
- [x] Migrate PVTResource to PVTController
- [ ] Migrate UserResource to UserController
- [ ] Migrate StampResource to StampResource
- [ ] Migrate SystemResource to SystemController
- [ ] Migrate ChartEventResource to ChartEventController
- [ ] Migrate ScheduleResource to ScheduleController
- [ ] Migrate LetterResource to LetterController
- [ ] Migrate MmlResource to MmlController
- [ ] Migrate NLabResource to NLabController
- [ ] Migrate AppoResource to AppoController
- [ ] Migrate ServerInfoResource to ServerInfoController
- [x] Replace JAX-RS annotations with Spring MVC annotations
- [x] Update @Context HttpServletRequest handling
- [x] Maintain identical API paths and response formats

#### Persistence Layer Migration
- [ ] Migrate persistence.xml to Spring Data JPA configuration
- [ ] Create Spring Data JPA repositories for each entity
- [ ] Convert custom JPA queries to @Query annotations
- [ ] Update EntityManager usage to repository interfaces
- [ ] Ensure entity relationships and mappings remain intact

### Phase 3: Advanced Features Migration
#### Asynchronous Processing
- [ ] Migrate @Asynchronous EJBs to @Async Spring services
- [ ] Configure Spring task executors
- [ ] Update async method calls

#### Messaging and Events
- [ ] Migrate CDI events to Spring Application Events
- [ ] Update ClaimSender, DiagnosisSender, MMLSender to Spring services
- [ ] Migrate message processing components

#### Templating
- [ ] Evaluate Velocity vs Thymeleaf migration
- [ ] Update template processing in services

#### MBeans and Monitoring
- [ ] Replace JBoss MBeans with Spring Actuator endpoints
- [ ] Configure health checks and metrics
- [ ] Migrate custom monitoring components

### Phase 4: Integration and Testing
#### Configuration Migration
- [ ] Migrate web.xml settings to Spring configuration
- [ ] Migrate beans.xml to @Configuration classes
- [ ] Handle servlet filters and listeners
- [ ] Configure CORS, security, and other web settings

#### Testing Setup
- [ ] Set up Spring Boot Test framework
- [ ] Write unit tests for migrated services
- [ ] Write integration tests for REST endpoints
- [ ] Set up test database configuration
- [ ] Test with PostgreSQL database

#### Client Compatibility
- [ ] Verify REST API contracts match original
- [ ] Test client-server communication
- [ ] Update client configuration if needed

### Phase 5: Deployment and Optimization
#### Packaging and Deployment
- [ ] Configure executable JAR packaging
- [ ] Set up embedded Tomcat/Jetty
- [ ] Create deployment scripts
- [ ] Configure production profiles

#### Performance Optimization
- [ ] Configure HikariCP connection pooling
- [ ] Optimize JPA/Hibernate settings
- [ ] Set up caching (Redis/Ehcache if needed)
- [ ] Performance testing and tuning

#### Monitoring and Operations
- [ ] Enable Spring Boot Actuator
- [ ] Configure logging and monitoring
- [ ] Set up health checks and alerts
- [ ] Documentation for operations team

## Key Migration Guidelines
- **Non-Intrusive Approach**: Do NOT modify any existing code in the current `server` module. Create a completely separate `server-spring` module for the new Spring Boot implementation.
- Maintain API compatibility for existing client
- Preserve all business logic functionality
- Update dependencies to latest stable versions
- Follow Spring Boot best practices
- Ensure backward compatibility where possible
- Run both implementations in parallel during transition period
- Gradually switch traffic from old to new server once fully tested

## Risk Assessment
- **High Risk**: REST API changes affecting client
- **Medium Risk**: Database query performance
- **Low Risk**: Service layer refactoring

## Success Criteria
- [ ] All REST endpoints functional and compatible
- [ ] All services migrated and tested
- [ ] Database operations working correctly
- [ ] Client can connect and operate normally
- [ ] Performance meets or exceeds current levels
- [ ] Deployment process simplified

## Timeline Estimate
- Phase 1: 1-2 weeks
- Phase 2: 2-3 weeks
- Phase 3: 1-2 weeks
- Phase 4: 1-2 weeks
- Phase 5: 1 week
- **Total**: 6-10 weeks

## Team Requirements
- Java/Spring Boot experience
- Understanding of current Jakarta EE architecture
- Testing and integration skills
- Database administration knowledge

## Next Steps
1. Review and approve this plan
2. Assign team members to phases
3. Set up development environment
4. Begin Phase 1 implementation
