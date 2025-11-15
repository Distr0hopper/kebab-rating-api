package com.fladenchef.rating.controller

import com.fladenchef.rating.model.dto.CreateUserRequestDto
import com.fladenchef.rating.model.dto.UpdateUserRequestDto
import com.fladenchef.rating.model.dto.UserResponseDto
import com.fladenchef.rating.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/users")
class UserController(
    private val userService: UserService,
) {

    // POST for creating a new user
    @PostMapping
    fun createUser(
        @Valid @RequestBody request: CreateUserRequestDto
    ) : ResponseEntity<UserResponseDto> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    // GET for retrieving a user
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponseDto>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    // GET for retrieving a user by ID
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserResponseDto> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }

    // GET for retrieving a user by username
   @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserResponseDto> {
         val user = userService.getUserByUsername(username)
         return ResponseEntity.ok(user)
    }

    // PUT for updating a user
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUser(id, request)
        return ResponseEntity.ok(updatedUser)
    }

    // DELETE for deleting a user
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}