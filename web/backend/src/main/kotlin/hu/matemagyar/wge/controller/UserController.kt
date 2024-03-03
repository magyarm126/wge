package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.User
import hu.matemagyar.wge.repository.UserRepository
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject

@Controller
class UserController {

    @Inject
    lateinit var userRepository: UserRepository

    @Get("/user/{id}")
    fun getUser(@QueryValue("id") id: Long): User {
        println("User request came in for Id: $id")
        return userRepository.save(User("User"))
    }

    @Get("/user")
    fun getUsers(): List<User> {
        println("Returning all users")
        return userRepository.findAll()
    }

    @Get("/current-user")
    fun getCurrentUser(): User {
        return userRepository.save(User("sampleUser"))
    }
}