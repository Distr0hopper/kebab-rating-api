package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.PriceRange
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePlaceRequestDto(
    @field:Size(max = 100, message = "Name too long")
    val name: String? = null,

    @field:Size(max = 255, message = "Address too long")
    val address: String? = null,

    @field:Size(max = 100, message = "City name too long")
    val city: String? = null,

    val priceRange: PriceRange? = null
)
