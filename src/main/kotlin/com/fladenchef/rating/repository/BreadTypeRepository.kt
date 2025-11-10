package com.fladenchef.rating.repository

import com.fladenchef.rating.model.entity.BreadType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BreadTypeRepository : JpaRepository<BreadType, UUID> {
    fun findByName(name: String): BreadType?

    fun existsByName(name: String): Boolean
}