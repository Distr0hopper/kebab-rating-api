package com.fladenchef.rating.model.dto

import jakarta.validation.constraints.*

data class UpdateReviewRequestDto(
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    val rating: Int? = null,

    @field:Size(max = 100, message = "Title too long")
    val title: String? = null,

    @field:Size(max = 1000, message = "Comment too long")
    val comment: String? = null
)