package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.User
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller
class UserController {

    @Get("/user/{id}")
    fun getUser(@QueryValue("id") id: Long): User {
        println("User request came in for Id: $id")
        return User("User")
    }

    @Get("/current-user")
    fun getCurrentUser(): User {
        return User.sampleUser()
    }
}