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
        val userentity = userRepository.save(UserEntity("User"))
        val user = UserDto()
        user.id = userentity.id
        user.name = userentity.name
        return user
    }

    fun getUsers(): List<UserDto> {
        println("Returning all users")
        return userRepository.findAll().stream().map {
            val user = UserDto()
            user.id = it.id
            user.name = it.name
            user

        }.collect(Collectors.toList())
    }


    fun getDto(): UserDto {
        println("Returning all users")
        val userentity = userRepository.findAll().get(0)
        val user = UserDto()
        user.id = userentity.id
        user.name = userentity.name
        return user
    }

    fun getCurrentUser(): UserDto {
        val userentity = userRepository.save(UserEntity("sampleUser"))
        val user = UserDto()
        user.id = userentity.id
        user.name = userentity.name
        return user
    }
}