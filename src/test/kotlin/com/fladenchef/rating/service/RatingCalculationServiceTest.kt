package com.fladenchef.rating.service

import com.fladenchef.rating.model.entity.*
import com.fladenchef.rating.model.enums.PriceRange
import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.PlaceRepository
import com.fladenchef.rating.repository.ReviewRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class RatingCalculationServiceTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var kebabVariantRepository: KebabVariantRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var service: RatingCalculationService

    private lateinit var place: Place
    private lateinit var breadType: BreadType
    private lateinit var meatType: MeatType

    @BeforeEach
    fun setUp() {
        reviewRepository = mockk()
        kebabVariantRepository = mockk()
        placeRepository = mockk()
        service = RatingCalculationService(reviewRepository, kebabVariantRepository, placeRepository)

        place = Place(
            id = UUID.randomUUID(),
            name = "Test Place",
            city = "Berlin",
            address = "Street 1",
            priceRange = PriceRange.MEDIUM
        )
        breadType = BreadType(id = UUID.randomUUID(), name = "Flatbread")
        meatType = MeatType(id = UUID.randomUUID(), name = "Veal")
    }

    @Test
    fun `should set kebab rating to 0 when no reviews`() {
        val kebabId = UUID.randomUUID()
        val kebab = KebabVariant(
            id = kebabId,
            place = place,
            name = "Classic",
            description = "Test",
            price = BigDecimal("6.00"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet(),
            averageRating = 4.5f,
            createdAt = Instant.now()
        )

        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebab)
        every { reviewRepository.findByKebabVariant(kebab) } returns emptyList()
        every { kebabVariantRepository.save(kebab) } returns kebab

        service.updateKebabRating(kebabId)

        assertEquals(0.0f, kebab.averageRating)
    }

    @Test
    fun `should compute kebab average rating correctly`() {
        val kebabId = UUID.randomUUID()
        val kebab = KebabVariant(
            id = kebabId,
            place = place,
            name = "Premium",
            description = "Test",
            price = BigDecimal("7.00"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = false,
            spicy = true,
            sauces = emptySet(),
            ingredients = emptySet(),
            averageRating = 0.0f,
            createdAt = Instant.now()
        )

        val r1 = mockk<Review>()
        val r2 = mockk<Review>()
        every { r1.rating } returns 4
        every { r2.rating } returns 2

        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebab)
        every { reviewRepository.findByKebabVariant(kebab) } returns listOf(r1, r2)
        every { kebabVariantRepository.save(kebab) } returns kebab

        service.updateKebabRating(kebabId)

        assertEquals(3.0f, kebab.averageRating)
    }

    @Test
    fun `should throw when kebab not found`() {
        val kebabId = UUID.randomUUID()
        every { kebabVariantRepository.findById(kebabId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            service.updateKebabRating(kebabId)
        }
    }

    @Test
    fun `should set place rating and review count to 0 when no kebabs`() {
        val placeId = place.id
        every { placeRepository.findById(placeId!!) } returns Optional.of(place)
        every { kebabVariantRepository.findByPlace(place) } returns emptyList()
        every { placeRepository.save(place) } returns place

        service.updatePlaceRating(placeId!!)

        assertEquals(0.0f, place.averageRating)
        assertEquals(0, place.reviewCount)
    }

    @Test
    fun `should compute place average rating and review count correctly`() {
        val placeId = place.id
        val kebabA = KebabVariant(
            id = UUID.randomUUID(),
            place = place,
            name = "A",
            description = "Test",
            price = BigDecimal("6.00"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet(),
            averageRating = 4.0f,
            createdAt = Instant.now()
        )
        val kebabB = KebabVariant(
            id = UUID.randomUUID(),
            place = place,
            name = "B",
            description = "Test",
            price = BigDecimal("5.50"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = true,
            spicy = true,
            sauces = emptySet(),
            ingredients = emptySet(),
            averageRating = 0.0f,
            createdAt = Instant.now()
        )

        val r1 = mockk<Review>()
        val r2 = mockk<Review>()
        val r3 = mockk<Review>()

        every { placeRepository.findById(placeId!!) } returns Optional.of(place)
        every { kebabVariantRepository.findByPlace(place) } returns listOf(kebabA, kebabB)
        every { reviewRepository.findByKebabVariant(kebabA) } returns listOf(r1, r2)
        every { reviewRepository.findByKebabVariant(kebabB) } returns listOf(r3)
        every { placeRepository.save(place) } returns place

        service.updatePlaceRating(placeId!!)

        assertEquals(4.0f, place.averageRating)
        assertEquals(3, place.reviewCount)
    }

    @Test
    fun `should set place rating to 0 when all kebabs have 0 rating`() {
        val placeId = place.id
        val kebabA = KebabVariant(
            id = UUID.randomUUID(),
            place = place,
            name = "A",
            description = "Test",
            price = BigDecimal("6.00"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet(),
            averageRating = 0.0f,
            createdAt = Instant.now()
        )
        val kebabB = kebabA.copy(id = UUID.randomUUID(), name = "B")

        val r1 = mockk<Review>()
        val r2 = mockk<Review>()

        every { placeRepository.findById(placeId!!) } returns Optional.of(place)
        every { kebabVariantRepository.findByPlace(place) } returns listOf(kebabA, kebabB)
        every { reviewRepository.findByKebabVariant(kebabA) } returns listOf(r1)
        every { reviewRepository.findByKebabVariant(kebabB) } returns listOf(r2)
        every { placeRepository.save(place) } returns place

        service.updatePlaceRating(placeId!!)

        assertEquals(0.0f, place.averageRating)
        assertEquals(2, place.reviewCount)
    }

    @Test
    fun `should throw when place not found`() {
        val placeId = UUID.randomUUID()
        every { placeRepository.findById(placeId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            service.updatePlaceRating(placeId)
        }
    }
}
