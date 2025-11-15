package com.fladenchef.rating.controller

import com.fladenchef.rating.model.dto.CreatePlaceRequestDto
import com.fladenchef.rating.model.dto.PlaceResponseDto
import com.fladenchef.rating.model.dto.UpdatePlaceRequestDto
import com.fladenchef.rating.service.PlaceService
import jakarta.validation.Valid
import org.apache.tomcat.util.net.openssl.ciphers.Cipher
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
@RequestMapping("api/places")
class PlaceController(
    private val placeService: PlaceService,
) {

    @PostMapping
    fun createPlace(
        @Valid @RequestBody request: CreatePlaceRequestDto
    ) : ResponseEntity<PlaceResponseDto> {
        val place = placeService.createPlace(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(place)
    }

    @GetMapping("/{id}")
    fun getPlaceById(@PathVariable id: UUID) : ResponseEntity<PlaceResponseDto> {
        val place = placeService.getPlaceById(id)
        return ResponseEntity.ok(place)
    }

    @GetMapping
    fun getAllPlaces(
        @RequestParam(required = false) city: String?
    ): ResponseEntity<List<PlaceResponseDto>> {
        val places = if (city != null) {
            placeService.getPlacesByCity(city)
        } else {
            placeService.getAllPlaces()
        }
        return ResponseEntity.ok(places)
    }

    @GetMapping("/top-rated")
    fun getTopRatedPlaces(
        @RequestParam(defaultValue = "4.0") minRating: Float
    ): ResponseEntity<List<PlaceResponseDto>> {
        val places = placeService.getTopRatedPlaces(minRating)
        return ResponseEntity.ok(places)
    }

    @PutMapping("/{id}")
    fun updatePlace(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePlaceRequestDto
    ): ResponseEntity<PlaceResponseDto> {
        val place = placeService.updatePlace(id, request)
        return ResponseEntity.ok(place)
    }

    @DeleteMapping("/{id}")
    fun deletePlace(@PathVariable id: UUID): ResponseEntity<Void> {
        placeService.deletePlace(id)
        return ResponseEntity.noContent().build()
    }
}