package com.fladenchef.rating.repository

import com.fladenchef.rating.model.entity.KebabVariant
import com.fladenchef.rating.model.entity.MeatType
import com.fladenchef.rating.model.entity.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface KebabVariantRepository : JpaRepository<MeatType, UUID> {

    // Find all kebabs from a specific place
    fun findByPlaceId(place: Place): List<MeatType>

    // Find vegetarian kebabs
    fun findByIsVegitarian(isVegetarian: Boolean): List<MeatType>

    // Find spicy kebabs
    fun findBySpicy(spicy: Boolean): List<MeatType>

    // Find kebabs with rating greater X
    fun findByAverageRatingGreaterThanEqual(minRating: Float): List<MeatType>

    // Top-rated kebabs (sorted by rating)
    fun findAllOrderByAverageRatingDesc(): List<MeatType>

    // Custom JPQL Query: Top N kebabs in a specific city
    @Query("""
        SELECT k FROM KebabVariant k
        WHERE k.place.city = :city
        AND k.averageRating > 0 
        ORDER BY k.averageRating DESC 
    """)
    fun findTopRatedInCity(city: String): List<KebabVariant>
}