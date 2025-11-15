package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreateUserRequestDto
import com.fladenchef.rating.model.dto.UpdateUserRequestDto
import com.fladenchef.rating.model.dto.UserResponseDto
import com.fladenchef.rating.repository.UserRepository
import jakarta.transaction.Transactional
import com.fladenchef.rating.model.entity.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class UserService (
    private val userRepository: UserRepository,
) {
    /*
        * CREATE operation *
        Creates a new user after validating the input data.
        Validations:
        - Username must be unique.
        - Email must be unique.
     */
    fun createUser(request: CreateUserRequestDto): UserResponseDto {
        // Validation: Is username already taken?
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username ${request.username} is already taken.")
        }

        // Validation: Is email already registered?
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email ${request.email} is already registered.")
        }

        //Hash password (for simplicity, using plain text here; in production use a proper hashing algorithm)
        val passwordHash = hashPassword(request.password) // TODO: Hash the password properly

        // Create entity
        val user = User(
            username = request.username,
            email = request.email,
            passwordHash = passwordHash
        )

        // Save entity
        val savedUser = userRepository.save(user)

        // Convert to DTO and return
        return savedUser.toDto()
    }

    /*
        * READ operations *
     */
    fun getUserById(id: UUID): UserResponseDto {
        val user = userRepository.findById(id).orElseThrow {
            IllegalArgumentException("User with id $id not found.")
        }
        return user.toDto()
    }

    fun getAllUsers(): List<UserResponseDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    fun getUserByUsername(username: String): UserResponseDto {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("User with username $username not found.")
        return user.toDto()
    }

    /*
        * UPDATE operations *
     */
    fun updateUser(id: UUID, request: UpdateUserRequestDto): UserResponseDto {
        // Find existing user
        val user = userRepository.findById(id).orElseThrow {
            IllegalArgumentException("User with id $id not found.")
        }

        // Validation: If username is being changed, check if new username is already taken
        if (request.username != user.username && userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username ${request.username} is already taken.")
        }

        // Validation: If email is being changed, check if new email is already registered by another user
        if(request.email != user.email) {
            val existingUserWithEmail = userRepository.findByEmail(request.email)
            if(existingUserWithEmail != null && existingUserWithEmail.id != id) {
                throw IllegalArgumentException("Email ${request.email} is already registered.")
            }
        }

        // Create updated user (data classes are immutable, so we create a new instance)
        val updatedUserEntity = user.copy(
            username = request.username,
            email = request.email
        )


        val updatedUser = userRepository.save(updatedUserEntity)
        return updatedUser.toDto()
    }

    /*
        * DELETE operation *
     */
    fun deleteUser(id: UUID) {
        // Check if user exists
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User with id $id not found.")
        }

        userRepository.deleteById(id)
    }


    private fun hashPassword(password: String): String {
        // For MVP: return the password as is
        return password
        // Alternative: Simple hash
        // return password.hashCode().toString()
    }

}