package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreateReviewRequestDto
import com.fladenchef.rating.model.dto.ReviewResponseDto
import com.fladenchef.rating.model.entity.Review
import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.ReviewRepository
import com.fladenchef.rating.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ReviewService (
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val kebabVariantRepository: KebabVariantRepository,
    private val ratingCalculationService: RatingCalculationService
){

    fun createReview(userId: UUID, request: CreateReviewRequestDto): ReviewResponseDto {
        // Load entities
        val user = userRepository.findById(userId)
            .orElseThrow{ throw NoSuchElementException("User not found with id $userId") }

        val kebabVariant = kebabVariantRepository.findById(request.kebabVariantId)
            .orElseThrow{ throw NoSuchElementException("Kebab variant not found with id ${request.kebabVariantId}") }

        // Validation: Does user have review for this kebab already?
        if(reviewRepository.existsByUserAndKebabVariant(user, kebabVariant)){
            throw IllegalArgumentException("User has already reviewed this kebab variant.")
        }

        // Create review
        val review = Review(
            user = user,
            kebabVariant = kebabVariant,
            rating = request.rating,
            title = request.title,
            comment = request.comment,
            createdAt = Instant.now()
        )

        // Save review to DB
        val savedReview = reviewRepository.save(review)

        // Calculate new ratings
        ratingCalculationService.updateKebabRating(kebabVariant.id!!)
        ratingCalculationService.updatePlaceRating(kebabVariant.place.id!!)

        return savedReview.toDto()
    }

    fun getReviewbyId(reviewId: UUID): ReviewResponseDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow{ throw NoSuchElementException("Review not found with id $reviewId") }

        return review.toDto()
    }

    fun getAllReviews(): List<ReviewResponseDto> {
        val reviews = reviewRepository.findAll()
        return reviews.map { it.toDto() }
    }

    fun getReviewsByUser(userId: UUID): List<ReviewResponseDto> {
        val user = userRepository.findById(userId)
            .orElseThrow{ throw NoSuchElementException("User not found with id $userId") }

        val reviews = reviewRepository.findByUser(user)
        return reviews.map { it.toDto() }
    }

    fun getReviewsByKebab(kebabId: UUID): List<ReviewResponseDto> {
        val kebab = kebabVariantRepository.findById(kebabId)
            .orElseThrow{ throw NoSuchElementException("Kebab variant not found with id $kebabId") }

        return reviewRepository.findByKebabVariant(kebab)
            .map { it.toDto() }
    }
}