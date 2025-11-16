# JWT-based Authentication Concept
## Architecture

1. HTTP Requests from clients include a JWT token in the Authorization header.
2. Spring Security Filter Chain extracts and validates the token with expiration date.
3. Controller checks user roles from the token for access control (USER, ADMIN).
4. Service layer processes requests based on user roles.


## Technical Details
- Add jwt dependency in build.gradle
- Configure security settings in SecurityConfig.java
  - Define public and protected endpoints (e.g., /api/auth/** public, /api/** protected).
  - Set up JWT token filter to validate tokens on each request.
- User Entity with new table for roles.