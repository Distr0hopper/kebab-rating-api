package com.fladenchef.rating.service

import com.fladenchef.rating.mapper.toDto
import com.fladenchef.rating.model.dto.CreateUserRequestDto
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

    private fun hashPassword(password: String): String {
        // For MVP: return the password as is
        return password
        // Alternative: Simple hash
        // return password.hashCode().toString()
    }

}