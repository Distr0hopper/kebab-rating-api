package com.fladenchef.rating.controller

import com.fladenchef.rating.model.dto.CreateReviewRequestDto
import com.fladenchef.rating.model.dto.ReviewResponseDto
import com.fladenchef.rating.service.ReviewService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/review")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping
    fun createReview(@RequestParam userId: UUID,
                     @Valid @RequestBody request: CreateReviewRequestDto
    ): ResponseEntity<ReviewResponseDto>
    {
        val review = reviewService.createReview(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    @GetMapping
    fun getAllReviews(): ResponseEntity<List<ReviewResponseDto>> {
        val reviews = reviewService.getAllReviews()
        return ResponseEntity.ok(reviews)
    }

    @GetMapping("/{id}")
    fun getReviewById(@PathVariable id: UUID): ResponseEntity<ReviewResponseDto> {
        val review = reviewService.getReviewbyId(id)
        return ResponseEntity.ok(review)
    }

    @GetMapping("/user/{userId}")
    fun getReviewsByUserId(@PathVariable userId: UUID): ResponseEntity<List<ReviewResponseDto>> {
        val reviews = reviewService.getReviewsByUser(userId)
        return ResponseEntity.ok(reviews)
    }

    @GetMapping("/kebab/{kebabId}")
    fun getReviewsByKebab(@PathVariable kebabId: UUID): ResponseEntity<List<ReviewResponseDto>> {
        val reviews = reviewService.getReviewsByKebab(kebabId)
        return ResponseEntity.ok(reviews)
    }

}