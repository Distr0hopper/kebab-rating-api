package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.PriceRange
import java.time.Instant
import java.util.*

data class PlaceResponseDto(
    val id: UUID,
    val name: String,
    val address: String,
    val city: String,
    val priceRange: PriceRange,
    val averageRating: Float,
    val reviewCount: Int,
    val createdAt: Instant
)