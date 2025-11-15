package com.fladenchef.rating.controller

import com.fladenchef.rating.model.dto.CreateKebabVariantRequestDto
import com.fladenchef.rating.model.dto.KebabVariantResponseDto
import com.fladenchef.rating.model.dto.UpdateKebabVariantRequestDto
import com.fladenchef.rating.service.KebabVariantService
import com.fladenchef.rating.service.PlaceService
import jakarta.validation.Valid
import org.aspectj.apache.bcel.Repository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/kebabs")
class KebabVariantController(
    private val kebabVariantService: KebabVariantService,
) {

    @PostMapping
    fun createKebab(
        @Valid @RequestBody request: CreateKebabVariantRequestDto
    ): ResponseEntity<KebabVariantResponseDto> {
        val kebab = kebabVariantService.createKebabVariant(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(kebab)
    }

    @GetMapping
    fun getAllKebabs(
        @RequestParam(required = false) vegetarian: Boolean?,
        @RequestParam(required = false) spicy: Boolean?
    ) : ResponseEntity<List<KebabVariantResponseDto>> {
        val kebabs = when {
            vegetarian == true -> kebabVariantService.getVegetarianKebabs()
            spicy == true -> kebabVariantService.getSpicyKebabs()
            else -> kebabVariantService.getAllKebabs()
        }
        return ResponseEntity.ok(kebabs)
    }

    @GetMapping("/{id}")
    fun getKebabbyId(@PathVariable id: UUID) : ResponseEntity<KebabVariantResponseDto> {
        val kebabs = kebabVariantService.getKebabById(id)
        return ResponseEntity.ok(kebabs)
    }

    @GetMapping("/place/{placeId}")
    fun getKebabsByPlaceId(@PathVariable placeId: UUID) : ResponseEntity<List<KebabVariantResponseDto>> {
        val kebabs = kebabVariantService.getKebabsByPlace(placeId)
        return ResponseEntity.ok(kebabs)
    }

    @GetMapping("/top")
    fun getTopRatedKebabs(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) minRating: Float?,
        @RequestParam(defaultValue = "10") limit: Int
    ) : ResponseEntity<List<KebabVariantResponseDto>> {
        val kebabs = when {
            city != null && minRating != null -> kebabVariantService.getTopRatedKebabsInCity(city, limit)
                .filter { it.averageRating >= minRating }
            minRating != null -> kebabVariantService.getTopRatedKebabsAboveRating(minRating, limit)
            city != null -> kebabVariantService.getTopRatedKebabsInCity(city, limit)
            else -> kebabVariantService.getTopRatedKebabs(limit)
        }
        return ResponseEntity.ok(kebabs)
    }

    // PUT for updating a kebab
    @PutMapping("/{id}")
    fun updateKebab(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateKebabVariantRequestDto
    ): ResponseEntity<KebabVariantResponseDto> {
        val kebab = kebabVariantService.updateKebabVariant(id, request)
        return ResponseEntity.ok(kebab)
    }

    // DELETE for deleting a kebab
    @DeleteMapping("/{id}")
    fun deleteKebab(@PathVariable id: UUID): ResponseEntity<Void> {
        kebabVariantService.deleteKebabVariant(id)
        return ResponseEntity.noContent().build()
    }
}