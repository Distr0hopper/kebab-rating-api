package com.fladenchef.rating.mapper

import com.fladenchef.rating.model.dto.ReviewResponseDto
import com.fladenchef.rating.model.entity.Review

/*
    * Mapper function to convert Review entity to ReviewResponseDto
 */
fun Review.toDto(): ReviewResponseDto {
    return ReviewResponseDto(
        id = this.id!!,
        username = this.user.username,
        kebabVariantName = this.kebabVariant.name,
        placeName = this.kebabVariant.place.name,
        rating = this.rating,
        title = this.title,
        comment = this.comment,
        createdAt = this.createdAt
    )
}