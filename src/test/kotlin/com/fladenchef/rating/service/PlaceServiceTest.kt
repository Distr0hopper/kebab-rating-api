package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreatePlaceRequestDto
import com.fladenchef.rating.model.dto.UpdatePlaceRequestDto
import com.fladenchef.rating.model.entity.Place
import com.fladenchef.rating.model.enums.PriceRange
import com.fladenchef.rating.repository.PlaceRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.Optional
import java.util.UUID

class PlaceServiceTest {

    private val placeRepository: PlaceRepository = mockk()
    private val placeService = PlaceService(placeRepository)

    @Test
    fun `should create place successfully`() {
        // Given
        val request = CreatePlaceRequestDto(
            name = "Test Place",
            address = "123 Test St",
            city = "Test City",
            priceRange = PriceRange.MEDIUM,
        )

        val successfulPlace = Place(
            id = UUID.randomUUID(),
            name = request.name,
            address = request.address,
            city = request.city,
            priceRange = request.priceRange,
            averageRating = 0.0f,
            reviewCount = 0,
            createdAt = Instant.now(),
        )

        // every
        every { placeRepository.save(any()) } returns successfulPlace

        // When
        val result = placeService.createPlace(request)
        // Then
        assertEquals(successfulPlace.toDto(), result)
    }

    @Test
    fun `should return place when id exists`(){
        // Given
        val existingPlace = Place(
            id = UUID.randomUUID(),
            name = "Existing Place",
            address = "456 Existing St",
            city = "Existing City",
            priceRange = PriceRange.EXPENSIVE,
            averageRating = 4.5f,
            reviewCount = 10,
            createdAt = Instant.now(),
        )
        every { placeRepository.findById(existingPlace.id!!) } returns java.util.Optional.of(existingPlace)
        // When
        val result = placeService.getPlaceById(existingPlace.id!!)
        // Then
        assertEquals(existingPlace.toDto(), result)
    }

    @Test
    fun `should throw exception when place not found`() {
        // Given
        val nonExistingId = UUID.randomUUID()

        // findById returns nothing for non-existing id
        every { placeRepository.findById(nonExistingId) } returns java.util.Optional.empty()

        // When + Then
        assertThrows<NoSuchElementException> {
            placeService.getPlaceById(nonExistingId)
        }
    }

    // getAllPlaces() Tests
    @Test
    fun `should return all places`() {
        // Given
        val place1 = Place(
            id = UUID.randomUUID(),
            name = "Place 1",
            address = "Address 1",
            city = "City A",
            priceRange = PriceRange.CHEAP,
            averageRating = 4.0f,
            reviewCount = 5,
            createdAt = Instant.now()
        )
        val place2 = Place(
            id = UUID.randomUUID(),
            name = "Place 2",
            address = "Address 2",
            city = "City B",
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.5f,
            reviewCount = 10,
            createdAt = Instant.now()
        )

        every { placeRepository.findAll() } returns listOf(place1, place2)

        // When
        val result = placeService.getAllPlaces()

        // Then
        assertEquals(2, result.size)
        assertEquals(place1.toDto(), result[0])
        assertEquals(place2.toDto(), result[1])
    }

    @Test
    fun `should return empty list when no places exist`() {
        // Given
        every { placeRepository.findAll() } returns emptyList()

        // When
        val result = placeService.getAllPlaces()

        // Then
        assertEquals(0, result.size)
    }

    // getPlacesByCity() Tests
    @Test
    fun `should return places for given city`() {
        // Given
        val city = "Berlin"
        val place1 = Place(
            id = UUID.randomUUID(),
            name = "Berlin Place 1",
            address = "Berlin Address 1",
            city = city,
            priceRange = PriceRange.CHEAP,
            averageRating = 4.0f,
            reviewCount = 5,
            createdAt = Instant.now()
        )
        val place2 = Place(
            id = UUID.randomUUID(),
            name = "Berlin Place 2",
            address = "Berlin Address 2",
            city = city,
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.5f,
            reviewCount = 10,
            createdAt = Instant.now()
        )

        every { placeRepository.findByCity(city) } returns listOf(place1, place2)

        // When
        val result = placeService.getPlacesByCity(city)

        // Then
        assertEquals(2, result.size)
        assertEquals(place1.toDto(), result[0])
        assertEquals(place2.toDto(), result[1])
    }

    @Test
    fun `should return empty list when no places in city`() {
        // Given
        val city = "Hamburg"
        every { placeRepository.findByCity(city) } returns emptyList()

        // When
        val result = placeService.getPlacesByCity(city)

        // Then
        assertEquals(0, result.size)
    }

    // getTopRatedPlaces() Tests
    @Test
    fun `should return places with rating greater than or equal to minimum`() {
        // Given
        val minRating = 4.5f
        val place1 = Place(
            id = UUID.randomUUID(),
            name = "High Rated Place",
            address = "Address 1",
            city = "City A",
            priceRange = PriceRange.EXPENSIVE,
            averageRating = 4.8f,
            reviewCount = 20,
            createdAt = Instant.now()
        )
        val place2 = Place(
            id = UUID.randomUUID(),
            name = "Good Rated Place",
            address = "Address 2",
            city = "City B",
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.5f,
            reviewCount = 15,
            createdAt = Instant.now()
        )

        every { placeRepository.findByAverageRatingGreaterThanEqual(minRating) } returns listOf(place1, place2)

        // When
        val result = placeService.getTopRatedPlaces(minRating)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return places sorted by rating descending`() {
        // Given
        val place1 = Place(
            id = UUID.randomUUID(),
            name = "Medium Rated",
            address = "Address 1",
            city = "City A",
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.3f,
            reviewCount = 10,
            createdAt = Instant.now()
        )
        val place2 = Place(
            id = UUID.randomUUID(),
            name = "High Rated",
            address = "Address 2",
            city = "City B",
            priceRange = PriceRange.EXPENSIVE,
            averageRating = 4.8f,
            reviewCount = 20,
            createdAt = Instant.now()
        )
        val place3 = Place(
            id = UUID.randomUUID(),
            name = "Low Rated",
            address = "Address 3",
            city = "City C",
            priceRange = PriceRange.CHEAP,
            averageRating = 4.0f,
            reviewCount = 5,
            createdAt = Instant.now()
        )

        every { placeRepository.findByAverageRatingGreaterThanEqual(4.0f) } returns listOf(place1, place2, place3)

        // When
        val result = placeService.getTopRatedPlaces()

        // Then
        assertEquals(3, result.size)
        assertEquals(4.8f, result[0].averageRating) // Höchstes Rating zuerst
        assertEquals(4.3f, result[1].averageRating)
        assertEquals(4.0f, result[2].averageRating) // Niedrigstes Rating zuletzt
    }

    @Test
    fun `should use default rating 4_0 when no parameter provided`() {
        // Given
        val place = Place(
            id = UUID.randomUUID(),
            name = "Good Place",
            address = "Address 1",
            city = "City A",
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.2f,
            reviewCount = 10,
            createdAt = Instant.now()
        )

        every { placeRepository.findByAverageRatingGreaterThanEqual(4.0f) } returns listOf(place)

        // When
        val result = placeService.getTopRatedPlaces() // Kein Parameter → Default 4.0f

        // Then
        assertEquals(1, result.size)
        assertEquals(place.toDto(), result[0])
    }

    @Test
    fun `should update place successfully`() {
        // Given
        val placeId = UUID.randomUUID()
        val existingPlace = Place(
            id = placeId,
            name = "Old Place",
            address = "Old Address 123",
            city = "Old City",
            priceRange = PriceRange.CHEAP,
            averageRating = 4.0f,
            reviewCount = 10,
            createdAt = Instant.now()
        )

        val updateRequest = UpdatePlaceRequestDto(
            name = "New Place",
            address = "New Address 456",
            city = "New City",
            priceRange = PriceRange.EXPENSIVE
        )

        val updatedPlace = existingPlace.copy(
            name = updateRequest.name,
            address = updateRequest.address,
            city = updateRequest.city,
            priceRange = updateRequest.priceRange
        )

        every { placeRepository.findById(placeId) } returns Optional.of(existingPlace)
        every { placeRepository.save(any()) } returns updatedPlace

        // When
        val result = placeService.updatePlace(placeId, updateRequest)

        // Then
        assertEquals("New Place", result.name)
        assertEquals("New Address 456", result.address)
        assertEquals("New City", result.city)
        assertEquals(PriceRange.EXPENSIVE, result.priceRange)
        // averageRating and reviewCount should remain unchanged
        assertEquals(4.0f, result.averageRating)
        assertEquals(10, result.reviewCount)
        verify { placeRepository.save(any()) }
    }

    @Test
    fun `should throw exception when updating non-existing place`() {
        // Given
        val placeId = UUID.randomUUID()
        val updateRequest = UpdatePlaceRequestDto(
            name = "Test Place",
            address = "Test Address",
            city = "Test City",
            priceRange = PriceRange.MEDIUM
        )

        every { placeRepository.findById(placeId) } returns Optional.empty()

        // When + Then
        assertThrows<NoSuchElementException> {
            placeService.updatePlace(placeId, updateRequest)
        }
    }

    @Test
    fun `should keep averageRating and reviewCount unchanged when updating place`() {
        // Given
        val placeId = UUID.randomUUID()
        val existingPlace = Place(
            id = placeId,
            name = "Place",
            address = "Address",
            city = "City",
            priceRange = PriceRange.MEDIUM,
            averageRating = 4.5f,
            reviewCount = 25,
            createdAt = Instant.now()
        )

        val updateRequest = UpdatePlaceRequestDto(
            name = "Updated Place",
            address = "Updated Address",
            city = "Updated City",
            priceRange = PriceRange.CHEAP
        )

        val updatedPlace = existingPlace.copy(
            name = updateRequest.name,
            address = updateRequest.address,
            city = updateRequest.city,
            priceRange = updateRequest.priceRange
        )

        every { placeRepository.findById(placeId) } returns Optional.of(existingPlace)
        every { placeRepository.save(any()) } returns updatedPlace

        // When
        val result = placeService.updatePlace(placeId, updateRequest)

        // Then
        assertEquals(4.5f, result.averageRating)
        assertEquals(25, result.reviewCount)
    }

    @Test
    fun `should delete place successfully`() {
        // Given
        val placeId = UUID.randomUUID()

        every { placeRepository.existsById(placeId) } returns true
        every { placeRepository.deleteById(placeId) } just Runs

        // When
        placeService.deletePlace(placeId)

        // Then
        verify { placeRepository.deleteById(placeId) }
    }

    @Test
    fun `should throw exception when deleting non-existing place`() {
        // Given
        val placeId = UUID.randomUUID()

        every { placeRepository.existsById(placeId) } returns false

        // When + Then
        assertThrows<NoSuchElementException> {
            placeService.deletePlace(placeId)
        }
    }

}