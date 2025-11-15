package com.fladenchef.rating.service

import com.fladenchef.rating.model.dto.CreateUserRequestDto
import com.fladenchef.rating.model.entity.User
import com.fladenchef.rating.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.UUID

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val userService = UserService(userRepository)

    @Test
    fun `should create user successfully`() {
        // Given
        val request = CreateUserRequestDto(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        val returnedUserAfterSave = User(
            id = UUID.randomUUID(),
            username = request.username,
            email = request.email,
            passwordHash = request.password,
            createdAt = Instant.now()
        )

        // Simulate that username and email do not exist
        every { userRepository.existsByUsername("testuser") } returns false
        every { userRepository.findByEmail("test@example.com") } returns null
        // If someone calls save on the mock, return the user we defined
        every { userRepository.save(any()) } returns returnedUserAfterSave

        // When saved (called on the mock)
        val result = userService.createUser(request)
        // Then (after save() is called on the mock repository)
        // behavior of mocks save() method was specified above - it returns returnedUserAfterSave
        // check the results
        assertEquals("testuser", result.username)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val request = CreateUserRequestDto(
            username = "existinguser",
            email = "exists@example.com",
            password = "password123"
        )

        // Simulating that the username already exists
        every { userRepository.existsByUsername("existinguser") } returns true

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.createUser(request)
        }
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Given
        val request = CreateUserRequestDto(
            username = "newuser",
            email = "existing@example.com",
            password = "password123"
        )

        val existingUser = User(
            id = UUID.randomUUID(),
            username = "otheruser",
            email = request.email,
            passwordHash = request.password,
            createdAt = Instant.now()
        )

        // Simulating that the email already exists
        every { userRepository.existsByUsername("newuser") } returns false
        // FindByEmail returns an existing user instead of null (because email exists)
        // normally it would return null if email does not exist
        every { userRepository.findByEmail("existing@example.com") } returns existingUser

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.createUser(request)
        }
    }
}