package com.fladenchef.rating.mapper

import com.fladenchef.rating.model.dto.PlaceResponseDto
import com.fladenchef.rating.model.entity.Place

fun Place.toDto(): PlaceResponseDto {
    return PlaceResponseDto(
        id = this.id!!,
        name = this.name,
        address = this.address,
        city = this.city,
        priceRange = this.priceRange,
        averageRating = this.averageRating,
        reviewCount = this.reviewCount,
        createdAt = this.createdAt
    )
}