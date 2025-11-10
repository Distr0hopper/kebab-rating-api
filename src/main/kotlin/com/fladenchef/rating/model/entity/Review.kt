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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kebab_variant_id" , nullable = false)
    val kebabVariant: KebabVariant,

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