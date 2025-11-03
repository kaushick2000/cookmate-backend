# Cook Mate - Backend

This is the backend for the Cook Mate Recipe Finder application.

Quick start:

1. Update `src/main/resources/application.properties` with your MySQL and email credentials.
2. Create the database `cookmate_db` in your local MySQL server.
3. Build and run:

```bash
mvn spring-boot:run
```

Default port: 8080

Notes:
- JWT secret is defined in `application.properties` (change to a secure value).
- This project includes core entities, repositories, basic auth with JWT, and skeleton services/controllers for extension.
