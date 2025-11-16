# Global Exception Handling
## Architecture
1. Controllers throw exceptions for error scenarios (e.g., ResourceNotFoundException).
2. A centralized `@RestControllerAdvice` class intercepts exceptions globally.
3. Custom error response objects are returned with appropriate HTTP status codes.

## Technical Details
- Create custom exception DTO.
    ```kotlin
    data class ErrorResponse(
        val timestamp: LocalDateTime,
        val status: Int,
        val error: String,
        val message: String?,
        val path: String
    )
    ```
- Implement a `GlobalExceptionHandler` class annotated with `@RestControllerAdvice`:
  ```kotlin
  @RestControllerAdvice
  class GlobalExceptionHandler {
  @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(...): ResponseEntity 
    // → HTTP 404
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(...): ResponseEntity
    // → HTTP 400
  
    // ... other handlers
  }
  ```
  
- Custom Exceptions:
    ```kotlin
    class ResourceNotFoundException(message: String) : NoSuchElementException(message)
    ```
- Example Error Response:
    ```json
    {
      "timestamp": "2024-01-01T12:00:00",
      "status": 404,
      "error": "Not Found",
      "message": "Place with ID 10 not found",
      "path": "/api/places/10"
    }
    ```