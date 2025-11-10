package com.fladenchef.rating.model.dto

import java.time.Instant
import java.util.UUID

// Response DTO for User entity
data class UserResponseDto (
    val id: UUID,
    val username: String,
    val email: String,
    val createdAt: Instant
)