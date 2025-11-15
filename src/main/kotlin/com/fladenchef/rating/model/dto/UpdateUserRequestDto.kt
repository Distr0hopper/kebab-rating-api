package com.fladenchef.rating.model.dto

import jakarta.validation.constraints.*

data class UpdateUserRequestDto (
    @field:NotBlank(message = "User name can not be blank")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
    val username: String,

    @field:NotBlank(message = "Email can not be blank")
    @field:Email(message = "Invalid Email-Adress")
    val email: String,
)