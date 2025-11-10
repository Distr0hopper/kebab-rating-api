package com.fladenchef.rating.model.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "bread_types")
data class BreadType (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String
)