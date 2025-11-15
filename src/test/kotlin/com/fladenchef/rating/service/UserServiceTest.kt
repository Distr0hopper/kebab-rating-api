package com.fladenchef.rating.service

import com.fladenchef.rating.model.dto.CreateUserRequestDto
import com.fladenchef.rating.model.dto.UpdateUserRequestDto
import com.fladenchef.rating.model.entity.User
import com.fladenchef.rating.repository.UserRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.Optional
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

    @Test
    fun `should update user successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val existingUser = User(
            id = userId,
            username = "oldusername",
            email = "old@example.com",
            passwordHash = "hash123",
            createdAt = Instant.now()
        )

        val updateRequest = UpdateUserRequestDto(
            username = "newusername",
            email = "new@example.com"
        )

        val updatedUser = existingUser.copy(
            username = updateRequest.username,
            email = updateRequest.email
        )

        // Mock repository behavior
        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUsername("newusername") } returns false
        every { userRepository.findByEmail("new@example.com") } returns null
        every { userRepository.save(any()) } returns updatedUser

        // When
        val result = userService.updateUser(userId, updateRequest)

        // Then
        assertEquals("newusername", result.username)
        assertEquals("new@example.com", result.email)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when updating user with existing username`() {
        // Given
        val userId = UUID.randomUUID()
        val existingUser = User(
            id = userId,
            username = "oldusername",
            email = "old@example.com",
            passwordHash = "hash123",
            createdAt = Instant.now()
        )

        val updateRequest = UpdateUserRequestDto(
            username = "takenusername",
            email = "new@example.com"
        )

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUsername("takenusername") } returns true

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.updateUser(userId, updateRequest)
        }
    }

    @Test
    fun `should throw exception when updating user with existing email`() {
        // Given
        val userId = UUID.randomUUID()
        val existingUser = User(
            id = userId,
            username = "oldusername",
            email = "old@example.com",
            passwordHash = "hash123",
            createdAt = Instant.now()
        )

        val otherUser = User(
            id = UUID.randomUUID(),
            username = "otheruser",
            email = "taken@example.com",
            passwordHash = "hash456",
            createdAt = Instant.now()
        )

        val updateRequest = UpdateUserRequestDto(
            username = "newusername",
            email = "taken@example.com"
        )

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUsername("newusername") } returns false
        every { userRepository.findByEmail("taken@example.com") } returns otherUser

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.updateUser(userId, updateRequest)
        }
    }

    @Test
    fun `should allow updating user with same username and email`() {
        // Given
        val userId = UUID.randomUUID()
        val existingUser = User(
            id = userId,
            username = "sameusername",
            email = "same@example.com",
            passwordHash = "hash123",
            createdAt = Instant.now()
        )

        val updateRequest = UpdateUserRequestDto(
            username = "sameusername",
            email = "same@example.com"
        )

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.existsByUsername("sameusername") } returns true
        every { userRepository.findByEmail("same@example.com") } returns existingUser
        every { userRepository.save(any()) } returns existingUser

        // When
        val result = userService.updateUser(userId, updateRequest)

        // Then
        assertEquals("sameusername", result.username)
        assertEquals("same@example.com", result.email)
    }

    @Test
    fun `should throw exception when updating non-existing user`() {
        // Given
        val userId = UUID.randomUUID()
        val updateRequest = UpdateUserRequestDto(
            username = "newusername",
            email = "new@example.com"
        )

        every { userRepository.findById(userId) } returns Optional.empty()

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.updateUser(userId, updateRequest)
        }
    }


    @Test
    fun `should delete user successfully`() {
        // Given
        val userId = UUID.randomUUID()

        every { userRepository.existsById(userId) } returns true
        every { userRepository.deleteById(userId) } just Runs
        // When
        userService.deleteUser(userId)
        // Then
        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `should throw exception when deleting non-existing user`() {
        // Given
        val userId = UUID.randomUUID()

        every { userRepository.existsById(userId) } returns false

        // When + Then
        assertThrows<IllegalArgumentException> {
            userService.deleteUser(userId)
        }
    }
}