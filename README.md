# 🍳 CookMate Backend

A robust Spring Boot REST API backend for the CookMate recipe management application, providing secure authentication, recipe CRUD operations, meal planning, shopping lists, and AI-powered recipe recommendations.

## ✨ Features

- **🔐 Authentication & Security**
  - JWT-based authentication
  - Spring Security integration
  - Password encryption with BCrypt
  - Email-based password reset
  - OAuth2 support for social logins
  - Role-based access control (USER, ADMIN)

- **📖 Recipe Management**
  - Create, read, update, delete recipes
  - Image upload and storage
  - Multi-category and cuisine support
  - Nutritional information tracking
  - Difficulty levels and cooking times
  - Recipe search and filtering

- **👤 User Management**
  - User registration and profile management
  - Email verification
  - Password reset functionality
  - User preferences and settings

- **❤️ Favorites System**
  - Save favorite recipes
  - Manage favorites collection
  - Quick access to preferred recipes

- **⭐ Reviews & Ratings**
  - Rate recipes (1-5 stars)
  - Write detailed reviews
  - View aggregated ratings

- **📅 Meal Planning**
  - Create custom meal plans
  - Schedule recipes for specific dates
  - Weekly/monthly planning views
  - Plan sharing capabilities

- **🛒 Shopping Lists**
  - Auto-generate from meal plans
  - Track recipe-based shopping items
  - Mark items as purchased
  - Organize by ingredient categories

- **🤖 AI Integration**
  - Recipe recommendations
  - Cooking assistance chatbot
  - Ingredient substitution suggestions

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **SMTP Server** (for email functionality)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cookmate-backends
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE cookmate_db;
   ```

3. **Configure application properties**
   
   Update `src/main/resources/application.properties`:
   
   ```properties
   # Server Configuration
   server.port=8080
   
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/cookmate_db
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   
   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   
   # JWT Configuration
   app.jwt.secret=your_secure_jwt_secret_key_change_this_in_production
   app.jwt.expiration=86400000
   
   # Email Configuration
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   
   # File Upload
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   
   # CORS Configuration
   app.cors.allowedOrigins=http://localhost:3000
   ```

4. **Initialize database schema** (optional - using SQL scripts)
   ```bash
   mysql -u root -p cookmate_db < db/cookmate_complete_schema.sql
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or with a specific profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

7. **Verify the server is running**
   
   Access: `http://localhost:8080`
   
   Health check: `http://localhost:8080/actuator/health` (if actuator enabled)

## 🛠️ Tech Stack

### Core Framework
- **Spring Boot 3.1.5** - Application framework
- **Java 17** - Programming language
- **Maven** - Build tool and dependency management

### Spring Modules
- **Spring Web** - REST API
- **Spring Data JPA** - Database access
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Input validation
- **Spring Mail** - Email functionality
- **Spring OAuth2 Client** - OAuth2 integration

### Database
- **MySQL 8.0** - Relational database
- **Hibernate** - ORM framework

### Security
- **JWT (jjwt 0.11.5)** - Token-based authentication
- **BCrypt** - Password hashing

### Utilities
- **Lombok** - Boilerplate code reduction
- **ModelMapper** - DTO mapping
- **Apache Commons Lang** - Utility functions

### Development Tools
- **Spring DevTools** - Hot reload

### Testing
- **Spring Boot Test** - Testing framework
- **Spring Security Test** - Security testing utilities
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework

## 📁 Project Structure

```
cookmate-backends/
├── src/
│   ├── main/
│   │   ├── java/com/cookmate/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── SecurityConfig.java      # Spring Security config
│   │   │   │   ├── CorsConfig.java          # CORS configuration
│   │   │   │   └── ModelMapperConfig.java   # Bean configurations
│   │   │   │
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AuthController.java      # Auth endpoints
│   │   │   │   ├── RecipeController.java    # Recipe CRUD
│   │   │   │   ├── UserController.java      # User management
│   │   │   │   ├── FavoriteController.java  # Favorites
│   │   │   │   ├── MealPlanController.java  # Meal planning
│   │   │   │   ├── ShoppingListController.java
│   │   │   │   ├── ReviewController.java    # Reviews
│   │   │   │   └── AIChatController.java    # AI chatbot
│   │   │   │
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/                 # Request DTOs
│   │   │   │   └── response/                # Response DTOs
│   │   │   │
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Recipe.java
│   │   │   │   ├── Ingredient.java
│   │   │   │   ├── Favorite.java
│   │   │   │   ├── MealPlan.java
│   │   │   │   ├── PlannedMeal.java
│   │   │   │   ├── ShoppingList.java
│   │   │   │   ├── ShoppingListItem.java
│   │   │   │   └── Review.java
│   │   │   │
│   │   │   ├── repository/          # JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RecipeRepository.java
│   │   │   │   ├── FavoriteRepository.java
│   │   │   │   ├── MealPlanRepository.java
│   │   │   │   ├── ShoppingListRepository.java
│   │   │   │   └── ReviewRepository.java
│   │   │   │
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── RecipeService.java
│   │   │   │   ├── FavoriteService.java
│   │   │   │   ├── MealPlanService.java
│   │   │   │   ├── ShoppingListService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── AIChatService.java
│   │   │   │
│   │   │   ├── security/            # Security Components
│   │   │   │   ├── JwtTokenProvider.java    # JWT utilities
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── UserDetailsServiceImpl.java
│   │   │   │   └── UserPrincipal.java
│   │   │   │
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── BadRequestException.java
│   │   │   │
│   │   │   └── util/                # Utility Classes
│   │   │
│   │   └── resources/
│   │       ├── application.properties       # Main config
│   │       ├── application-dev.properties   # Dev profile
│   │       └── application-prod.properties  # Prod profile
│   │
│   └── test/                        # Test classes
│       └── java/com/cookmate/
│
├── db/                              # Database scripts
│   ├── cookmate_complete_schema.sql         # Complete schema
│   ├── insert_sample_recipes.sql            # Sample data
│   └── migration/                           # Migration scripts
│
├── uploads/                         # Uploaded files (recipe images)
│
├── pom.xml                          # Maven configuration
├── mvnw                             # Maven wrapper (Unix)
├── mvnw.cmd                         # Maven wrapper (Windows)
└── README.md                        # This file
```

## 🔌 API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/register` | Register new user | No |
| POST | `/login` | User login | No |
| POST | `/forgot-password` | Request password reset | No |
| POST | `/reset-password` | Reset password with token | No |
| POST | `/verify-email` | Verify email address | No |

### Users (`/api/users`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/me` | Get current user | Yes |
| PUT | `/me` | Update user profile | Yes |
| PUT | `/me/password` | Change password | Yes |

### Recipes (`/api/recipes`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get all recipes (paginated) | No |
| GET | `/{id}` | Get recipe by ID | No |
| GET | `/search` | Search recipes | No |
| POST | `/` | Create new recipe | Yes |
| PUT | `/{id}` | Update recipe | Yes (Owner) |
| DELETE | `/{id}` | Delete recipe | Yes (Owner) |
| POST | `/{id}/image` | Upload recipe image | Yes (Owner) |

### Favorites (`/api/favorites`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get user's favorites | Yes |
| POST | `/{recipeId}` | Add to favorites | Yes |
| DELETE | `/{recipeId}` | Remove from favorites | Yes |

### Meal Plans (`/api/meal-plans`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get user's meal plans | Yes |
| GET | `/{id}` | Get meal plan by ID | Yes |
| POST | `/` | Create meal plan | Yes |
| PUT | `/{id}` | Update meal plan | Yes |
| DELETE | `/{id}` | Delete meal plan | Yes |
| POST | `/{id}/meals` | Add meal to plan | Yes |
| DELETE | `/meals/{mealId}` | Remove meal from plan | Yes |

### Shopping Lists (`/api/shopping-lists`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get shopping lists | Yes |
| GET | `/{id}` | Get shopping list by ID | Yes |
| POST | `/from-meal-plan/{planId}` | Generate from meal plan | Yes |
| PUT | `/items/{itemId}` | Update item (mark purchased) | Yes |
| DELETE | `/{id}` | Delete shopping list | Yes |

### Reviews (`/api/reviews`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/recipe/{recipeId}` | Get reviews for recipe | No |
| POST | `/recipe/{recipeId}` | Add review | Yes |
| PUT | `/{id}` | Update review | Yes (Owner) |
| DELETE | `/{id}` | Delete review | Yes (Owner) |

### AI Chat (`/api/ai`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/chat` | Send message to AI | Yes |

## 🔒 Security

### JWT Authentication Flow

1. User logs in with credentials
2. Server validates and returns JWT token
3. Client stores token (cookie/localStorage)
4. Client includes token in `Authorization: Bearer <token>` header
5. Server validates token on each request
6. Token expires after 24 hours (configurable)

### Password Security

- Passwords hashed using **BCrypt** (strength 10)
- Minimum 8 characters, requires uppercase, lowercase, number, special char
- Password reset tokens expire after 1 hour

### CORS Configuration

Configured to allow requests from frontend origin (`http://localhost:3000`).

Update in `CorsConfig.java` or `application.properties`:

```properties
app.cors.allowedOrigins=http://localhost:3000,https://yourdomain.com
```

## 🗄️ Database Schema

Key entities and relationships:

- **User** (1) ←→ (N) **Recipe** - User creates recipes
- **User** (1) ←→ (N) **Favorite** ←→ (1) **Recipe** - User favorites recipes
- **User** (1) ←→ (N) **MealPlan** - User creates meal plans
- **MealPlan** (1) ←→ (N) **PlannedMeal** ←→ (1) **Recipe** - Planned meals reference recipes
- **User** (1) ←→ (N) **ShoppingList** - User creates shopping lists
- **ShoppingList** (1) ←→ (N) **ShoppingListItem** - Items in shopping list
- **User** (1) ←→ (N) **Review** ←→ (1) **Recipe** - User reviews recipes

See `db/cookmate_complete_schema.sql` for full schema.

## 📧 Email Configuration

### Gmail Setup

1. Enable 2-factor authentication
2. Generate App Password: [Google Account > Security > App Passwords](https://myaccount.google.com/apppasswords)
3. Update `application.properties`:
   ```properties
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_16_char_app_password
   ```

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn -Dtest=RecipeServiceTest test
```

### Run with coverage
```bash
mvn clean test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

## 🚢 Deployment

### Build for production
```bash
mvn clean package -DskipTests
```

Generated JAR: `target/cookmate-backend-0.0.1-SNAPSHOT.jar`

### Run production JAR
```bash
java -jar target/cookmate-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment (Example)

```dockerfile
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/cookmate-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t cookmate-backend .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod cookmate-backend
```

## 🛠️ Common Issues & Troubleshooting

### Port 8080 already in use
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Database connection errors
- Verify MySQL is running
- Check credentials in `application.properties`
- Ensure database `cookmate_db` exists
- Verify MySQL version compatibility (8.0+)

### JWT token errors
- Ensure `app.jwt.secret` is set (min 32 characters)
- Check token expiration time
- Verify token format in requests: `Authorization: Bearer <token>`

### Email sending failures
- Verify SMTP credentials
- Check firewall/antivirus blocking port 587
- Enable "Less secure app access" or use App Password (Gmail)

## 📊 Performance Optimization

- **Database indexing** on frequently queried columns (user_id, recipe_id, etc.)
- **Pagination** for large result sets
- **Caching** with Spring Cache (Redis recommended for production)
- **Connection pooling** with HikariCP (default in Spring Boot)

## 🔐 Production Checklist

- [ ] Change JWT secret to strong random value
- [ ] Update CORS allowed origins to production domain
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (not `update`)
- [ ] Configure proper database backup strategy
- [ ] Enable HTTPS/SSL
- [ ] Set up logging and monitoring
- [ ] Configure rate limiting
- [ ] Review and test all security configurations
- [ ] Set appropriate file upload size limits
- [ ] Configure production email settings

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Kaushick** - Initial work

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Contact: attendanceproject1411@gmail.com

## 🙏 Acknowledgments

- Spring Boot community
- JWT library maintainers
- All contributors and testers
