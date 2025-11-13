package com.fladenchef.rating.service

import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.PlaceRepository
import com.fladenchef.rating.repository.ReviewRepository
import com.fladenchef.rating.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID


@Service
@Transactional
class RatingCalculationService(
    private val reviewRepository: ReviewRepository,
    private val kebabVariantRepository: KebabVariantRepository,
    private val placeRepository: PlaceRepository,
) {

    fun updateKebabRating(kebabId: UUID){
        val kebab = kebabVariantRepository.findById(kebabId)
            .orElseThrow{ throw NoSuchElementException("Kebab variant not found for id $kebabId") }

        val reviews = reviewRepository.findByKebabVariant(kebab)

        if (reviews.isEmpty()){
            kebab.averageRating = 0.0f
        } else {
            val averageRating = reviews.map { it.rating }.average().toFloat()
            kebab.averageRating = averageRating
        }

        kebabVariantRepository.save(kebab)
    }

    fun updatePlaceRating(placeId: UUID){
        var place = placeRepository.findById(placeId)
            .orElseThrow{ throw NoSuchElementException("Place not found for id $placeId") }

        val kebabs = kebabVariantRepository.findByPlace(place)

        if (kebabs.isEmpty()){
            place.averageRating = 0.0f
            place.reviewCount = 0
        } else {
            // Average of all kebab ratings
            val ratingsWithValues = kebabs.filter { it.averageRating > 0.0f }

            if (ratingsWithValues.isEmpty()) {
                place.averageRating = 0.0f
            } else {
                val averageRating = ratingsWithValues.map { it.averageRating }.average().toFloat()
                place.averageRating = averageRating
            }

            // Total number of reviews across all kebabs
            place.reviewCount = kebabs.sumOf {
                reviewRepository.findByKebabVariant(it).size
            }
        }

        placeRepository.save(place)
    }


}