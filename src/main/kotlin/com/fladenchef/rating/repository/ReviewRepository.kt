package com.fladenchef.rating.repository

import com.fladenchef.rating.model.entity.KebabVariant
import com.fladenchef.rating.model.entity.Review
import com.fladenchef.rating.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReviewRepository : JpaRepository<Review, UUID> {

    // Find all reviews by a specific user
    fun findByUser(userId: User): List<Review>

    // Find all reviews for a specific kebab variant
    fun findByKebabVariant(kebabVariant: KebabVariant): List<Review>

    // Check if user has already reviewed a specific kebab variant
    fun existsByUserAndKebabVariant(user: User, kebabVariant: KebabVariant): Boolean

    // Find review by specific user for a specific kebab variant
    fun findByUserAndKebabVariant(user: User, kebabVariant: KebabVariant): Review?

    // Newest reviews first
    fun findAllByOrderByCreatedAtDesc(): List<Review>

    // Reviews with rating >= X
    fun findByRatingGreaterThanEqual(minRating: Float): List<Review>
}