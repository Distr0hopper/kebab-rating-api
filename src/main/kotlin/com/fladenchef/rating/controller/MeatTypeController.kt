package com.fladenchef.rating.controller

import com.fladenchef.rating.model.entity.MeatType
import com.fladenchef.rating.repository.MeatTypeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/meat-types")
class MeatTypeController(
    private val meatTypeRepository: MeatTypeRepository
) {
    // GET-Only queries!
    @GetMapping
    fun getAllMeatTypes(): ResponseEntity<List<MeatType>> {
        return ResponseEntity.ok(meatTypeRepository.findAll())
    }
}