package com.fladenchef.rating.model.dto
import jakarta.validation.constraints.*


data class CreateUserRequestDto(
    @field:NotBlank(message="Username must not be blank")
    @field:Size(min=3, max=50, message="Username must be between 3 and 50 characters long")
    val userName: String,

    @field:NotBlank(message="Email must not be blank")
    @field:Email(message = "Invalid Email-Adress")
    val email: String,

    @field:NotBlank(message="Password must not be blank")
    @field:Size(min=8, message="Password must be at least 8 characters long")
    val password: String
)
