package com.fladenchef.rating.model.dto

import jakarta.validation.constraints.*

data class UpdateUserRequestDto (
    // Username optional
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
    val username: String? = null,

    // Email optional
    @field:Email(message = "Invalid Email-Adress")
    val email: String? = null,
)