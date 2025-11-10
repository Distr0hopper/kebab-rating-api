package com.fladenchef.rating.model.dto

import jakarta.validation.constraints.*
import java.util.UUID

data class CreateReviewRequestDto(
    @field:NotNull(message = "KebabVariant ID must not be null")
    val kebabVariantId: UUID,

    @field:NotNull(message = "Rating must not be null")
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    val rating: Int,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(max = 100, message = "Title too long")
    val title: String,

    @field:NotBlank(message = "Comment must not be blank")
    @field:Size(max = 1000, message = "Comment too long")
    val comment: String
)
