package com.fladenchef.rating.service

import com.fladenchef.rating.model.dto.CreateKebabVariantRequestDto
import com.fladenchef.rating.model.entity.*
import com.fladenchef.rating.model.enums.Ingredients
import com.fladenchef.rating.model.enums.PriceRange
import com.fladenchef.rating.model.enums.Sauces
import com.fladenchef.rating.repository.BreadTypeRepository
import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.MeatTypeRepository
import com.fladenchef.rating.repository.PlaceRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.collections.emptyList

class KebabVariantServiceTest {

    // Mocks + SUT
    private lateinit var kebabVariantRepository: KebabVariantRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var breadTypeRepository: BreadTypeRepository
    private lateinit var meatTypeRepository: MeatTypeRepository
    private lateinit var kebabVariantService: KebabVariantService

    // Gemeinsame Testdaten
    private lateinit var placeId: UUID
    private lateinit var breadTypeId: UUID
    private lateinit var meatTypeId: UUID
    private lateinit var place: Place
    private lateinit var breadType: BreadType
    private lateinit var meatType: MeatType

    @BeforeEach
    fun setUp() {
        kebabVariantRepository = mockk()
        placeRepository = mockk()
        breadTypeRepository = mockk()
        meatTypeRepository = mockk()

        kebabVariantService = KebabVariantService(
            kebabVariantRepository,
            placeRepository,
            breadTypeRepository,
            meatTypeRepository
        )

        placeId = UUID.randomUUID()
        breadTypeId = UUID.randomUUID()
        meatTypeId = UUID.randomUUID()

        place = Place(
            id = placeId,
            name = "Test Dönerladen",
            city = "Berlin",
            address = "Teststr. 1",
            priceRange = PriceRange.MEDIUM
        )

        breadType = BreadType(
            id = breadTypeId,
            name = "Fladenbrot",
        )

        meatType = MeatType(
            id = meatTypeId,
            name = "Hähnchen",
        )
    }

    @Test
    fun `should create kebab variant successfully`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Hähnchen Döner",
            description = "Lecker",
            price = BigDecimal("6.50"),
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = true,
            sauces = setOf(Sauces.HOT_SAUCE, Sauces.GARLIC),
            ingredients = setOf(Ingredients.SALAD, Ingredients.TOMATO)
        )

        val savedKebab = KebabVariant(
            id = UUID.randomUUID(),
            place = place,
            name = "Hähnchen Döner",
            description = "Lecker",
            price = BigDecimal("6.50"),
            breadType = breadType,
            meatType = meatType,
            isVegetarian = false,
            spicy = true,
            sauces = setOf(Sauces.HOT_SAUCE, Sauces.GARLIC),
            ingredients = setOf(Ingredients.SALAD, Ingredients.TOMATO),
            averageRating = 0.0f,
            createdAt = Instant.now()
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { breadTypeRepository.findById(breadTypeId) } returns Optional.of(breadType)
        every { meatTypeRepository.findById(meatTypeId) } returns Optional.of(meatType)
        every { kebabVariantRepository.save(any()) } returns savedKebab

        val result = kebabVariantService.createKebabVariant(request)

        assertNotNull(result)
        assertEquals("Hähnchen Döner", result.name)
        assertEquals(BigDecimal("6.50"), result.price)
        assertEquals(true, result.spicy)
        verify { kebabVariantRepository.save(any()) }
    }

    @Test
    fun `should throw exception when place not found`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Test",
            description = "Test",
            price = BigDecimal("5.00"),
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet()
        )

        every { placeRepository.findById(placeId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            kebabVariantService.createKebabVariant(request)
        }
    }

    @Test
    fun `should throw exception when bread type not found`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Test",
            description = "Test",
            price = BigDecimal("5.00"),
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet()
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { breadTypeRepository.findById(breadTypeId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            kebabVariantService.createKebabVariant(request)
        }
    }

    @Test
    fun `should throw exception when meat type not found`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Test",
            description = "Test",
            price = BigDecimal("5.00"),
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet()
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { breadTypeRepository.findById(breadTypeId) } returns Optional.of(breadType)
        every { meatTypeRepository.findById(meatTypeId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            kebabVariantService.createKebabVariant(request)
        }
    }

    @Test
    fun `should throw exception when price is zero`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Test",
            description = "Test",
            price = BigDecimal.ZERO,
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet()
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { breadTypeRepository.findById(breadTypeId) } returns Optional.of(breadType)
        every { meatTypeRepository.findById(meatTypeId) } returns Optional.of(meatType)

        assertThrows<IllegalArgumentException> {
            kebabVariantService.createKebabVariant(request)
        }
    }

    @Test
    fun `should throw exception when price is negative`() {
        val request = CreateKebabVariantRequestDto(
            placeId = placeId,
            name = "Test",
            description = "Test",
            price = BigDecimal("-5.00"),
            breadTypeId = breadTypeId,
            meatTypeId = meatTypeId,
            isVegetarian = false,
            spicy = false,
            sauces = emptySet(),
            ingredients = emptySet()
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { breadTypeRepository.findById(breadTypeId) } returns Optional.of(breadType)
        every { meatTypeRepository.findById(meatTypeId) } returns Optional.of(meatType)

        assertThrows<IllegalArgumentException> {
            kebabVariantService.createKebabVariant(request)
        }
    }

    @Test
    fun `should get kebab by id successfully`() {
        val kebabId = UUID.randomUUID()
        val kebab = KebabVariant(
            id = kebabId,
            place = place,
            name = "Test Döner",
            description = "Test",
            price = BigDecimal("5.50"),
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

        val result = kebabVariantService.getKebabById(kebabId)

        assertNotNull(result)
        assertEquals(kebabId, result.id)
        assertEquals("Test Döner", result.name)
    }

    @Test
    fun `should throw exception when kebab not found by id`() {
        val kebabId = UUID.randomUUID()
        every { kebabVariantRepository.findById(kebabId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            kebabVariantService.getKebabById(kebabId)
        }
    }

    @Test
    fun `should return all kebabs`() {
        val kebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner 1",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.0f,
                createdAt = Instant.now()
            ),
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner 2",
                description = "Test",
                price = BigDecimal("6.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = true,
                spicy = true,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.5f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findAll() } returns kebabs

        val result = kebabVariantService.getAllKebabs()

        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list when no kebabs exist`() {
        every { kebabVariantRepository.findAll() } returns emptyList()

        val result = kebabVariantService.getAllKebabs()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return kebabs by place`() {
        val kebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.0f,
                createdAt = Instant.now()
            )
        )

        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { kebabVariantRepository.findByPlace(place) } returns kebabs

        val result = kebabVariantService.getKebabsByPlace(placeId)

        assertEquals(1, result.size)
        assertEquals("Döner", result[0].name)
    }

    @Test
    fun `should throw exception when place not found for kebabs by place`() {
        every { placeRepository.findById(placeId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            kebabVariantService.getKebabsByPlace(placeId)
        }
    }

    @Test
    fun `should return empty list when place has no kebabs`() {
        every { placeRepository.findById(placeId) } returns Optional.of(place)
        every { kebabVariantRepository.findByPlace(place) } returns emptyList()

        val result = kebabVariantService.getKebabsByPlace(placeId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return vegetarian kebabs`() {
        val vegKebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Veggie Döner",
                description = "Vegetarisch",
                price = BigDecimal("5.50"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = true,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.2f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findByIsVegetarian(true) } returns vegKebabs

        val result = kebabVariantService.getVegetarianKebabs()

        assertEquals(1, result.size)
        assertTrue(result[0].isVegetarian)
    }

    @Test
    fun `should return empty list when no vegetarian kebabs exist`() {
        every { kebabVariantRepository.findByIsVegetarian(true) } returns emptyList()

        val result = kebabVariantService.getVegetarianKebabs()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return spicy kebabs`() {
        val spicyKebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Scharfer Döner",
                description = "Extra scharf",
                price = BigDecimal("6.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = true,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.7f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findBySpicy(true) } returns spicyKebabs

        val result = kebabVariantService.getSpicyKebabs()

        assertEquals(1, result.size)
        assertTrue(result[0].spicy)
    }

    @Test
    fun `should return empty list when no spicy kebabs exist`() {
        every { kebabVariantRepository.findBySpicy(true) } returns emptyList()

        val result = kebabVariantService.getSpicyKebabs()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return top rated kebabs with default limit`() {
        val kebabs = (1..15).map {
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner $it",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = (5.0f - it * 0.1f),
                createdAt = Instant.now()
            )
        }

        every { kebabVariantRepository.findAllByOrderByAverageRatingDesc() } returns kebabs

        val result = kebabVariantService.getTopRatedKebabs()

        assertEquals(10, result.size)
    }

    @Test
    fun `should return top rated kebabs with custom limit`() {
        val kebabs = (1..10).map {
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner $it",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = (5.0f - it * 0.1f),
                createdAt = Instant.now()
            )
        }

        every { kebabVariantRepository.findAllByOrderByAverageRatingDesc() } returns kebabs

        val result = kebabVariantService.getTopRatedKebabs(5)

        assertEquals(5, result.size)
    }

    @Test
    fun `should return top rated kebabs above minimum rating`() {
        val kebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Top Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.8f,
                createdAt = Instant.now()
            ),
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Good Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.5f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findByAverageRatingGreaterThanEqual(4.5f) } returns kebabs

        val result = kebabVariantService.getTopRatedKebabsAboveRating(4.5f)

        assertEquals(2, result.size)
        assertTrue(result[0].averageRating >= 4.5f)
    }

    @Test
    fun `should return top rated kebabs sorted by rating descending`() {
        val kebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Good Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.5f,
                createdAt = Instant.now()
            ),
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Top Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.8f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findByAverageRatingGreaterThanEqual(4.0f) } returns kebabs

        val result = kebabVariantService.getTopRatedKebabsAboveRating(4.0f)

        assertEquals(4.8f, result[0].averageRating)
        assertEquals(4.5f, result[1].averageRating)
    }

    @Test
    fun `should return top rated kebabs in city`() {
        val kebabs = listOf(
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Berlin Döner",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = 4.9f,
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findTopRatedInCity("Berlin") } returns kebabs

        val result = kebabVariantService.getTopRatedKebabsInCity("Berlin")

        assertEquals(1, result.size)
        assertEquals("Berlin Döner", result[0].name)
    }

    @Test
    fun `should return top rated kebabs in city with custom limit`() {
        val kebabs = (1..15).map {
            KebabVariant(
                id = UUID.randomUUID(),
                place = place,
                name = "Döner $it",
                description = "Test",
                price = BigDecimal("5.00"),
                breadType = breadType,
                meatType = meatType,
                isVegetarian = false,
                spicy = false,
                sauces = emptySet(),
                ingredients = emptySet(),
                averageRating = (5.0f - it * 0.1f),
                createdAt = Instant.now()
            )
        }

        every { kebabVariantRepository.findTopRatedInCity("Berlin") } returns kebabs

        val result = kebabVariantService.getTopRatedKebabsInCity("Berlin", 5)

        assertEquals(5, result.size)
    }

    @Test
    fun `should return empty list when no top rated kebabs in city`() {
        every { kebabVariantRepository.findTopRatedInCity("Hamburg") } returns emptyList()

        val result = kebabVariantService.getTopRatedKebabsInCity("Hamburg")

        assertTrue(result.isEmpty())
    }
}
