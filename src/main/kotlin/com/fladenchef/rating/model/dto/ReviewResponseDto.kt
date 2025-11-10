package com.fladenchef.rating.model.dto

import java.time.Instant
import java.util.*

data class ReviewResponseDto(
    val id: UUID,
    val username: String,
    val kebabVariantName: String,
    val placeName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val createdAt: Instant
)