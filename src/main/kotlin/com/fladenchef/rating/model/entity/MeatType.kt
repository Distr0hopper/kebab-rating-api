package com.fladenchef.rating.model.entity

import jakarta.persistence.*
import java.util.UUID


@Entity
@Table(name = "meat_type")
data class MeatType(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,

    @Column(nullable = false)
    val isHalal: Boolean = false
)
