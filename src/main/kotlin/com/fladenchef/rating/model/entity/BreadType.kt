package com.fladenchef.rating.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "bread_types")
data class BreadType (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false, unique = true, length = 100)
    val name: String
)