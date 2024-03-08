package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.UserDto
import hu.matemagyar.wge.service.UserService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject

@Controller
class UserController {

    @Inject
    lateinit var userService: UserService

    @Get("/user/{id}")
    fun getUser(@QueryValue("id") id: Long): UserDto {
        println("User request came in for Id: $id")
        return userService.getUser(id)
    }

    @Get("/user")
    fun getUsers(): List<UserDto> {
        println("Returning all users")
        return userService.getUsers()
    }

    @Get("/current-user")
    fun getCurrentUser(): UserDto {
        return userService.saveUser()
    }
}