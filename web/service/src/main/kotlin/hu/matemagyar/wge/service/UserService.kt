package hu.matemagyar.wge.service

import hu.matemagyar.wge.entity.UserEntity
import hu.matemagyar.wge.model.UserDto
import hu.matemagyar.wge.repository.UserRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.stream.Collectors

@Singleton
class UserService {

    @Inject
    lateinit var userRepository: UserRepository

    fun getUser(id: Long): UserDto {
        println("User request came in for Id: $id")
        return toUserDto(userRepository.save(UserEntity("User")))
    }

    fun getUsers(): List<UserDto> {
        println("Returning all users")
        return userRepository.findAll().stream().map(this::toUserDto).collect(Collectors.toList())
    }

    fun saveUser(): UserDto {
        return toUserDto(userRepository.save(UserEntity("sampleUser")))
    }

    private fun toUserDto(userEntity: UserEntity): UserDto {
        val user = UserDto()
        user.id = userEntity.id
        user.name = userEntity.name
        return user
    }
}