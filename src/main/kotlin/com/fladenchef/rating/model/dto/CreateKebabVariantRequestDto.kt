package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.Ingredients
import com.fladenchef.rating.model.enums.Sauces
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*

data class CreateKebabVariantRequestDto(
    @field:NotNull(message = "Place ID must not be null")
    val placeId: UUID,

    @field:NotBlank(message = "Name must not be blank")
    @field:Size(max = 100, message = "Name too long")
    val name: String,

    @field:Size(max = 500, message = "Description too long")
    val description: String? = null,

    @field:NotNull(message = "Price must not be null")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    val price: BigDecimal,

    @field:NotNull(message = "BreadType ID must not be null")
    val breadTypeId: UUID,

    @field:NotNull(message = "MeatType ID must not be null")
    val meatTypeId: UUID,

    val isVegetarian: Boolean = false,

    val spicy: Boolean = false,

    val sauces: Set<Sauces> = emptySet(),

    val ingredients: Set<Ingredients> = emptySet()
)