package com.fladenchef.rating.model.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "review")
data class Review (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val variantId: UUID,

    @Column(nullable = false)
    val rating: Int, // 1-5

    @Column(nullable = false, length = 100)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val comment: String,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
){
    init {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
    }
}