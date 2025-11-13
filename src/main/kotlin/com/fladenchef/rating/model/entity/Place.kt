package com.fladenchef.rating.model.entity

import com.fladenchef.rating.model.enums.PriceRange
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "places")
data class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, length = 200)
    val name: String,

    @Column(nullable = false, length = 255)
    val address: String,

    @Column(nullable = false, length = 100)
    val city: String,

    @Column(nullable = false)
    var averageRating: Float = 0.0f,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priceRange: PriceRange,

    @Column(nullable = false)
    var reviewCount: Int = 0,

    @Column(nullable = true, updatable = false)
    val createdAt: Instant = Instant.now(),
)
