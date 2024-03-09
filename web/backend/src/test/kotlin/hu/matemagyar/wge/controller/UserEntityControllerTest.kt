package hu.matemagyar.wge.controller

import io.micronaut.http.HttpRequest
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
        val request: HttpRequest<Any> = HttpRequest.GET("/user/1")
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)
    }

    @Test
    fun getCurrentUser() {
        val request: HttpRequest<Any> = HttpRequest.GET("/current-user")
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)
    }
}