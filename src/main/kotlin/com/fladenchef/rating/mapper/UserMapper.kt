package com.fladenchef.rating.mapper

import com.fladenchef.rating.model.dto.UserResponseDto
import com.fladenchef.rating.model.entity.User

fun User.toDto(): UserResponseDto {
    return UserResponseDto(
        id = this.id!!,
        username = this.username,
        email = this.email,
        createdAt = this.createdAt
    )
}