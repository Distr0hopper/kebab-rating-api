package com.fladenchef.rating.repository

import com.fladenchef.rating.model.entity.Place
import com.fladenchef.rating.model.enums.PriceRange
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaceRepository : JpaRepository<Place, UUID> {
    // Find all places in a specific city
    fun findByCity(city: String): List<Place>

    // Find places within a specific price range
    fun findByPriceRange(priceRange: PriceRange): List<Place>

    // Find places with rating greater X
    fun findByAverageRatingGreaterThanEqual(minRating: Float): List<Place>

    // Find Top-Rated places in a City
    fun findByCityOrderByAverageRatingDesc(city: String): List<Place>
}