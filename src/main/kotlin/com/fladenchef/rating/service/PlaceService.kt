package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreatePlaceRequestDto
import com.fladenchef.rating.model.dto.PlaceResponseDto
import com.fladenchef.rating.model.entity.Place
import com.fladenchef.rating.repository.PlaceRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class PlaceService (
    private val placeRepository: PlaceRepository // Need to use the repository bean for database access
){
    fun createPlace(request: CreatePlaceRequestDto): PlaceResponseDto {
        val place = Place(
            name = request.name,
            address = request.address,
            city = request.city,
            priceRange = request.priceRange,
            averageRating = 0.0f,
            reviewCount = 0,
            createdAt = Instant.now()
        )
        val savePlace = placeRepository.save(place)
        return savePlace.toDto()
    }

    fun getPlaceById(id: UUID): PlaceResponseDto {
        val place = placeRepository.findById(id).orElseThrow(){
            throw NoSuchElementException("Place with id $id does not exist") }
        return place.toDto()
    }

    fun getAllPlaces(): List<PlaceResponseDto> {
        return placeRepository.findAll().map { it.toDto() }
    }

    fun getPlacesByCity(city: String): List<PlaceResponseDto> {
        return placeRepository.findByCity(city).map { it.toDto() }
    }

    fun getTopRatedPlaces(minRating: Float = 4.0f): List<PlaceResponseDto> {
        return placeRepository.findByAverageRatingGreaterThanEqual(minRating).sortedByDescending { it.averageRating }.map { it.toDto() }
    }
}