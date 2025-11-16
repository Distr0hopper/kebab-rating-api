# Database Migration Concept
## Architecture

1. Flyway migration checks on startup
   2. Check history table for applied migrations
   3. Apply pending migration scripts in order
   

## Technical Details
- Add Flyway dependency in build.gradle
- Add migration scripts in `src/main/resources/db/migration`
  - Naming convention: V1__Initial_Setup.sql, V2__Add_New_Table.sql
  - Example intial migration script:
    ```sql
    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50) NOT NULL,
        password VARCHAR(100) NOT NULL,
        role VARCHAR(20) NOT NULL
    );
    
    /* Create Bread Types */
    /* Create Kebab Variants */ 
    /* ... */
    ```
  - Configure Flyway in application.properties