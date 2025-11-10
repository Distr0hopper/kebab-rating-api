package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.Ingredients
import com.fladenchef.rating.model.enums.Sauces
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class KebabVariantResponseDto(
    val id: UUID,
    val placeName: String,          // Nur Name, nicht ganze Place!
    val placeCity: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val breadTypeName: String,      // Nur Name
    val meatTypeName: String,       // Nur Name
    val isVegetarian: Boolean,
    val spicy: Boolean,
    val sauces: Set<String>,        // Als Display-Namen
    val ingredients: Set<String>,   // Als Display-Namen
    val averageRating: Float,
    val createdAt: Instant
)