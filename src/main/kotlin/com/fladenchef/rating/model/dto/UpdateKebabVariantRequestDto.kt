package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.Ingredients
import com.fladenchef.rating.model.enums.Sauces
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*


data class UpdateKebabVariantRequestDto(
    @field:Size(max = 100, message = "Name too long")
    val name: String? = null,

    @field:Size(max = 500, message = "Description too long")
    val description: String? = null,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    val price: BigDecimal? = null,

    val breadTypeId: UUID? = null,

    val meatTypeId: UUID? = null,

    val isVegetarian: Boolean? = null,

    val spicy: Boolean? = null,

    val sauces: Set<Sauces>? = null,

    val ingredients: Set<Ingredients>? = null

    )
