package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.UserDto
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

@MicronautTest
class UserEntityControllerTest {

    @Inject
    @Client("/")
    lateinit var client: HttpClient

    @Test
    fun getUser() {
        val request: HttpRequest<UserDto> = HttpRequest.GET<UserDto?>("/user/1").accept(MediaType.APPLICATION_JSON)
        val body = client.toBlocking().retrieve(request, UserDto::class.java)
        assertNotNull(body)
    }

    @Test
    fun getCurrentUser() {
        val request: HttpRequest<UserDto> = HttpRequest.GET("/current-user")
        val body = client.toBlocking().retrieve(request, UserDto::class.java)
        assertNotNull(body)
    }
}