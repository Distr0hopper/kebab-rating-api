package com.fladenchef.rating.controller

import com.fladenchef.rating.model.entity.BreadType
import com.fladenchef.rating.repository.BreadTypeRepository
import com.fladenchef.rating.service.KebabVariantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bread-types")
class BreadTypeController(
    private val breadTypeRepository: BreadTypeRepository
) {
    // GET-Only queries!
    @GetMapping
    fun getAllBreadTypes(): ResponseEntity<List<BreadType>>{
        return ResponseEntity.ok(breadTypeRepository.findAll())
    }

}