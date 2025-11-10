package com.fladenchef.rating.mapper

import com.fladenchef.rating.model.dto.KebabVariantResponseDto
import com.fladenchef.rating.model.entity.KebabVariant
/*
    * Mapper function to convert KebabVariant entity to KebabVariantResponseDto
 */
fun KebabVariant.toDto(): KebabVariantResponseDto {
    return KebabVariantResponseDto(
        id = this.id!!,
        placeName = this.place.name,
        placeCity = this.place.city,
        name = this.name,
        description = this.description,
        price = this.price,
        breadTypeName = this.breadType.name,
        meatTypeName = this.meatType.name,
        isVegetarian = this.isVegetarian,
        spicy = this.spicy,
        sauces = this.sauces.map { it.displayName() }.toSet(), // Enum => String
        ingredients = this.ingredients.map { it.displayName() }.toSet(), // Enum => String
        averageRating = this.averageRating,
        createdAt = this.createdAt
    )
}