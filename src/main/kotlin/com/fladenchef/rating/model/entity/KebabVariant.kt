package com.fladenchef.rating.model.entity

import com.fladenchef.rating.model.enums.Ingredients
import com.fladenchef.rating.model.enums.Sauces
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "kebab_variants")
data class KebabVariant(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val placeId: UUID,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(length = 500)
    val description: String? = null,

    @Column(nullable = false)
    val price: Float = 0.0f,

    @Column(nullable = false)
    val breadType: UUID,

    @Column(nullable = false)
    val meatType: UUID,

    @Column(nullable = false)
    val isVegetarian: Boolean = false,

    @Column(nullable = false)
    val spicy: Boolean = false,

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "kebab_variant_ingredients",
        joinColumns = [JoinColumn(name = "kebab_variant_id")]
    )
    @Column(name = "ingredient")
    val ingredients: Set<Ingredients> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER) // create a separate table for sauces as a collection (set/list)
    @Enumerated(EnumType.STRING) //save as string in db
    // Give the ElementCollection a name and join column
    @CollectionTable(
        name = "kebab_variant_sauces",
        joinColumns = [JoinColumn(name = "kebab_variant_id")] // foreign key to kebab_variants table*
    )
    @Column(name = "sauce")
    val sauces: Set<Sauces> = emptySet(),

    @Column(nullable = false)
    var averageRating: Float = 0.0f,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
