package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreateKebabVariantRequestDto
import com.fladenchef.rating.model.dto.KebabVariantResponseDto
import com.fladenchef.rating.model.dto.UpdateKebabVariantRequestDto
import com.fladenchef.rating.model.entity.KebabVariant
import com.fladenchef.rating.repository.BreadTypeRepository
import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.MeatTypeRepository
import com.fladenchef.rating.repository.PlaceRepository
import com.fladenchef.rating.repository.ReviewRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class KebabVariantService (
    private val kebabVariantRepository: KebabVariantRepository,
    private val placeRepository: PlaceRepository,
    private val breadTypeRepository: BreadTypeRepository,
    private val meatTypeRepository: MeatTypeRepository,
    private val reviewRepository: ReviewRepository
){

    /*
            * Create-Operations
         */
    fun createKebabVariant(request: CreateKebabVariantRequestDto): KebabVariantResponseDto{
        // Load entities
        var place = placeRepository.findById(request.placeId)
            .orElseThrow{ throw NoSuchElementException("Place not found with id ${request.placeId}") }

        var breadType = breadTypeRepository.findById(request.breadTypeId)
            .orElseThrow{ throw NoSuchElementException("Bread type not found with id ${request.breadTypeId}") }

        var meatType = meatTypeRepository.findById(request.meatTypeId)
            .orElseThrow{ throw NoSuchElementException("Meat type not found with id ${request.meatTypeId}") }

        // Validation: Price positive?
        if(request.price <= java.math.BigDecimal.ZERO){
            throw IllegalArgumentException("Price must be positive.")
        }

        // Create entity
        val kebab = KebabVariant(
            place = place,
            name = request.name,
            description = request.description,
            price = request.price,
            breadType = breadType,
            meatType = meatType,
            isVegetarian = request.isVegetarian,
            spicy = request.spicy,
            sauces = request.sauces,
            ingredients = request.ingredients,
            averageRating = 0.0f,
            createdAt = Instant.now()
        )

        val savedKebab = kebabVariantRepository.save(kebab)
        return savedKebab.toDto()
    }

    /*
        * Read-Operations
     */

    fun getKebabById(id: UUID): KebabVariantResponseDto {
        val kebab = kebabVariantRepository.findById(id).orElseThrow {
            throw NoSuchElementException("Kebab variant with id $id not found.")
        }
        return kebab.toDto()
    }

    fun getAllKebabs(): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findAll().map { it.toDto() }
    }

    fun getKebabsByPlace(placeId: UUID): List<KebabVariantResponseDto> {
        val place = placeRepository.findById(placeId).orElseThrow {
            throw NoSuchElementException("Place with id $placeId not found.")
        }
        return kebabVariantRepository.findByPlace(place).map { it.toDto() }
    }

    fun getVegetarianKebabs(): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findByIsVegetarian(true).map { it.toDto() }
    }

    fun getSpicyKebabs(): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findBySpicy(true).map { it.toDto() }
    }

    fun getTopRatedKebabs(limit: Int = 10): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findAllByOrderByAverageRatingDesc()
            .take(limit)
            .map { it.toDto() }
    }

    fun getTopRatedKebabsAboveRating(minRating: Float, limit: Int = 10): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findByAverageRatingGreaterThanEqual(minRating)
            .sortedByDescending { it.averageRating }
            .take(limit)
            .map { it.toDto() }
    }

    fun getTopRatedKebabsInCity(city: String, limit: Int = 10): List<KebabVariantResponseDto> {
        return kebabVariantRepository.findTopRatedInCity(city)
            .take(limit)
            .map { it.toDto() }
    }

    /*
     * Update-Operations
     */
    fun updateKebabVariant(id: UUID, request: UpdateKebabVariantRequestDto): KebabVariantResponseDto {
        // Find existing kebab
        val existingKebab = kebabVariantRepository.findById(id).orElseThrow {
            throw NoSuchElementException("Kebab variant with id $id not found.")
        }

        // Load new entities
        val breadType = breadTypeRepository.findById(request.breadTypeId)
            .orElseThrow { throw NoSuchElementException("Bread type not found with id ${request.breadTypeId}") }

        val meatType = meatTypeRepository.findById(request.meatTypeId)
            .orElseThrow { throw NoSuchElementException("Meat type not found with id ${request.meatTypeId}") }

        // Validation: Price positive?
        if (request.price <= java.math.BigDecimal.ZERO) {
            throw IllegalArgumentException("Price must be positive.")
        }

        // Create updated kebab (data classes are immutable)
        val updatedKebab = existingKebab.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            breadType = breadType,
            meatType = meatType,
            isVegetarian = request.isVegetarian,
            spicy = request.spicy,
            sauces = request.sauces,
            ingredients = request.ingredients
        )

        val savedKebab = kebabVariantRepository.save(updatedKebab)
        return savedKebab.toDto()
    }

    /*
     * Delete-Operations
     */
    fun deleteKebabVariant(id: UUID) {
        // Check if kebab exists
        val kebab = kebabVariantRepository.findById(id).orElseThrow {
            throw NoSuchElementException("Kebab variant with id $id not found.")
        }

        // Need to delete associated reviews first due to foreign key constraints
        reviewRepository.deleteAllByKebabVariant(kebab)
        kebabVariantRepository.deleteById(id)
    }
}