package com.fladenchef.rating.service

import com.fladenchef.rating.model.dto.CreateReviewRequestDto
import com.fladenchef.rating.model.entity.BreadType
import com.fladenchef.rating.model.entity.KebabVariant
import com.fladenchef.rating.model.entity.MeatType
import com.fladenchef.rating.model.entity.Place
import com.fladenchef.rating.model.entity.Review
import com.fladenchef.rating.model.entity.User
import com.fladenchef.rating.model.enums.PriceRange
import com.fladenchef.rating.repository.KebabVariantRepository
import com.fladenchef.rating.repository.ReviewRepository
import com.fladenchef.rating.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class ReviewServiceTest {

    // Mocks + SUT
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var userRepository: UserRepository
    private lateinit var kebabVariantRepository: KebabVariantRepository
    private lateinit var ratingCalculationService: RatingCalculationService
    private lateinit var reviewService: ReviewService

    // Shared Testdata (created fresh before each test)
    private lateinit var userId: UUID
    private lateinit var placeId: UUID
    private lateinit var kebabId: UUID
    private lateinit var user: User
    private lateinit var place: Place
    private lateinit var kebabVariant: KebabVariant

    @BeforeEach
    fun setUp() {
        // New mocks per test
        reviewRepository = mockk()
        userRepository = mockk()
        kebabVariantRepository = mockk()
        ratingCalculationService = mockk(relaxed = true)

        reviewService = ReviewService(
            reviewRepository,
            userRepository,
            kebabVariantRepository,
            ratingCalculationService
        )


        userId = UUID.randomUUID()
        placeId = UUID.randomUUID()
        kebabId = UUID.randomUUID()

        user = User(id = userId, username = "testuser", email = "test@test.de", passwordHash = "hash")
        place = Place(
            id = placeId,
            name = "Test Place",
            city = "Berlin",
            address = "Teststr. 1",
            priceRange = PriceRange.EXPENSIVE
        )
        kebabVariant = KebabVariant(
            id = kebabId,
            name = "Döner",
            place = place,
            price = BigDecimal("5.00"),
            breadType = mockk<BreadType>(),
            meatType = mockk<MeatType>()
        )
    }

    @Test
    fun `should create review successfully`() {
        val request = CreateReviewRequestDto(
            kebabVariantId = kebabId,
            rating = 5,
            title = "Super",
            comment = "Lecker"
        )
        val savedReview = Review(
            id = UUID.randomUUID(),
            user = user,
            kebabVariant = kebabVariant,
            rating = 5,
            title = "Super",
            comment = "Lecker",
            createdAt = Instant.now()
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebabVariant)
        every { reviewRepository.existsByUserAndKebabVariant(user, kebabVariant) } returns false
        every { reviewRepository.save(any()) } returns savedReview

        val result = reviewService.createReview(userId, request)

        assertNotNull(result)
        assertEquals(5, result.rating)
        verify { reviewRepository.save(any()) }
        verify { ratingCalculationService.updateKebabRating(kebabId) }
        verify { ratingCalculationService.updatePlaceRating(placeId) }
    }

    @Test
    fun `should throw exception when user not found`() {
        val request = CreateReviewRequestDto(
            kebabVariantId = kebabId,
            rating = 5,
            title = "Test",
            comment = "Test"
        )

        every { userRepository.findById(userId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            reviewService.createReview(userId, request)
        }
    }

    @Test
    fun `should throw exception when kebab variant not found`() {
        val request = CreateReviewRequestDto(
            kebabVariantId = kebabId,
            rating = 5,
            title = "Test",
            comment = "Test"
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { kebabVariantRepository.findById(kebabId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            reviewService.createReview(userId, request)
        }
    }

    @Test
    fun `should throw exception when user already reviewed kebab`() {
        val request = CreateReviewRequestDto(
            kebabVariantId = kebabId,
            rating = 5,
            title = "Test",
            comment = "Test"
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebabVariant)
        every { reviewRepository.existsByUserAndKebabVariant(user, kebabVariant) } returns true

        assertThrows<IllegalArgumentException> {
            reviewService.createReview(userId, request)
        }
    }

    @Test
    fun `should get review by id successfully`() {
        val reviewId = UUID.randomUUID()
        val otherUser = User(id = UUID.randomUUID(), username = "u", email = "u@test.de", passwordHash = "hash")
        val otherPlace = Place(
            id = UUID.randomUUID(),
            name = "P",
            city = "Berlin",
            address = "Str. 1",
            priceRange = PriceRange.CHEAP
        )
        val otherKebab = KebabVariant(
            id = UUID.randomUUID(),
            name = "Dürüm",
            place = otherPlace,
            price = BigDecimal("6.00"),
            breadType = mockk(),
            meatType = mockk()
        )
        val review = Review(
            id = reviewId,
            user = otherUser,
            kebabVariant = otherKebab,
            rating = 5,
            title = "Super",
            comment = "Lecker",
            createdAt = Instant.now()
        )

        every { reviewRepository.findById(reviewId) } returns Optional.of(review)

        val result = reviewService.getReviewbyId(reviewId)

        assertNotNull(result)
        assertEquals(reviewId, result.id)
    }

    @Test
    fun `should throw exception when review not found`() {
        val reviewId = UUID.randomUUID()
        every { reviewRepository.findById(reviewId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            reviewService.getReviewbyId(reviewId)
        }
    }

    @Test
    fun `should return all reviews`() {
        val user1 = User(id = UUID.randomUUID(), username = "user1", email = "u1@test.de", passwordHash = "hash")
        val user2 = User(id = UUID.randomUUID(), username = "user2", email = "u2@test.de", passwordHash = "hash")
        val place1 = Place(
            id = UUID.randomUUID(),
            name = "Place1",
            city = "Berlin",
            address = "Addr1",
            priceRange = PriceRange.CHEAP
        )
        val kebab1 = KebabVariant(
            id = UUID.randomUUID(),
            name = "Döner1",
            place = place1,
            price = BigDecimal("5.00"),
            breadType = mockk(relaxed = true),
            meatType = mockk(relaxed = true)
        )
        val kebab2 = KebabVariant(
            id = UUID.randomUUID(),
            name = "Döner2",
            place = place1,
            price = BigDecimal("6.00"),
            breadType = mockk(relaxed = true),
            meatType = mockk(relaxed = true)
        )

        val reviews = listOf(
            Review(
                id = UUID.randomUUID(),
                user = user1,
                kebabVariant = kebab1,
                rating = 5,
                title = "Test1",
                comment = "Comment1",
                createdAt = Instant.now()
            ),
            Review(
                id = UUID.randomUUID(),
                user = user2,
                kebabVariant = kebab2,
                rating = 4,
                title = "Test2",
                comment = "Comment2",
                createdAt = Instant.now()
            )
        )

        every { reviewRepository.findAll() } returns reviews

        val result = reviewService.getAllReviews()

        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list when no reviews exist`() {
        every { reviewRepository.findAll() } returns emptyList()

        val result = reviewService.getAllReviews()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return reviews by user`() {
        val otherPlace = Place(
            id = UUID.randomUUID(),
            name = "Place",
            city = "Berlin",
            address = "Addr",
            priceRange = PriceRange.CHEAP
        )
        val otherKebab = KebabVariant(
            id = UUID.randomUUID(),
            name = "Döner",
            place = otherPlace,
            price = BigDecimal("5.00"),
            breadType = mockk(relaxed = true),
            meatType = mockk(relaxed = true)
        )

        val reviews = listOf(
            Review(
                id = UUID.randomUUID(),
                user = user,
                kebabVariant = otherKebab,
                rating = 5,
                title = "Test",
                comment = "Comment",
                createdAt = Instant.now()
            )
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { reviewRepository.findByUser(user) } returns reviews

        val result = reviewService.getReviewsByUser(userId)

        assertEquals(1, result.size)
    }

    @Test
    fun `should return empty list when user has no reviews`() {
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { reviewRepository.findByUser(user) } returns emptyList()

        val result = reviewService.getReviewsByUser(userId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return reviews by kebab`() {
        val otherUser = User(id = UUID.randomUUID(), username = "other", email = "other@test.de", passwordHash = "hash")

        val reviews = listOf(
            Review(
                id = UUID.randomUUID(),
                user = otherUser,
                kebabVariant = kebabVariant,
                rating = 5,
                title = "Test",
                comment = "Comment",
                createdAt = Instant.now()
            )
        )

        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebabVariant)
        every { reviewRepository.findByKebabVariant(kebabVariant) } returns reviews

        val result = reviewService.getReviewsByKebab(kebabId)

        assertEquals(1, result.size)
    }

    @Test
    fun `should return empty list when kebab has no reviews`() {
        every { kebabVariantRepository.findById(kebabId) } returns Optional.of(kebabVariant)
        every { reviewRepository.findByKebabVariant(kebabVariant) } returns emptyList()

        val result = reviewService.getReviewsByKebab(kebabId)

        assertTrue(result.isEmpty())
    }
}
