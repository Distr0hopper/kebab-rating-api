package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreateUserRequestDto
import com.fladenchef.rating.model.dto.UpdateUserRequestDto
import com.fladenchef.rating.model.dto.UserResponseDto
import com.fladenchef.rating.repository.UserRepository
import jakarta.transaction.Transactional
import com.fladenchef.rating.model.entity.User
import com.fladenchef.rating.repository.ReviewRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class UserService (
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
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

        // Validate and compute new username
        val newUsername = request.username?.let { newName ->
            if (newName != user.username && userRepository.existsByUsername(newName)) {
                throw IllegalArgumentException("Username $newName is already taken.")
            }
            newName
        } ?: user.username

        // Validate and compute new email
        val newEmail = request.email?.let { newMail ->
            if (newMail != user.email) {
                val existingUserWithEmail = userRepository.findByEmail(newMail)
                if (existingUserWithEmail != null && existingUserWithEmail.id != id) {
                    throw IllegalArgumentException("Email $newMail is already registered.")
                }
            }
            newMail
        } ?: user.email

        // If nothing changed, return as is
        if (newUsername == user.username && newEmail == user.email) {
            return user.toDto()
        }

        // Create updated user (data classes are immutable, so we create a new instance)
        val updatedUserEntity = user.copy(
            username = newUsername,
            email = newEmail
        )


        val updatedUser = userRepository.save(updatedUserEntity)
        return updatedUser.toDto()
    }

    /*
        * DELETE operation *
     */
    fun deleteUser(id: UUID) {
        // Check if user exists
        val user = userRepository.findById(id).orElseThrow {
            IllegalArgumentException("User with id $id not found.")
        }

        // First delete all reviews associated with the user
        reviewRepository.deleteAllByUser(user)

        // Then delete the user
        userRepository.deleteById(id)
    }


    private fun hashPassword(password: String): String {
        // For MVP: return the password as is
        return password
        // Alternative: Simple hash
        // return password.hashCode().toString()
    }

}