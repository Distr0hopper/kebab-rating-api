package com.fladenchef.rating.repository

import com.fladenchef.rating.model.entity.MeatType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeatTypeRepository : JpaRepository<MeatType, UUID> {
    fun findByName(name: String): MeatType?

    fun findByIsHalal(isHalal: Boolean): List<MeatType>
}