package com.fladenchef.rating.model.dto

import com.fladenchef.rating.model.enums.PriceRange
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePlaceRequestDto(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(max = 100, message = "Name too long")
    val name: String,

    @field:NotBlank(message = "Address must not be blank")
    @field:Size(max = 255, message = "Address too long")
    val address: String,

    @field:NotBlank(message = "City must not be blank")
    @field:Size(max = 100, message = "City name too long")
    val city: String,

    val priceRange: PriceRange
)
