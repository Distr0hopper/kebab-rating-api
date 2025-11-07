package com.fladenchef.rating.model.entity

import jakarta.persistence.*


@Entity
@Table(name = "meat_type")
data class MeatType(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false, unique = true, length = 100)
    val name: String,

    @Column(nullable = false)
    val isHalal: Boolean = false
)
