package hu.matemagyar.wge.controller

import hu.matemagyar.wge.http.codec.ProtoBufferCodec
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
class UserControllerTest {

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
    fun getScene() {
        val request: HttpRequest<Any> = HttpRequest.GET<Any?>("/sceneJson").accept(MediaType.APPLICATION_JSON)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)
    }

    @Test
    fun getHybrid() {
        val request: HttpRequest<Any> = HttpRequest.GET<Any?>("/sceneHybrid").accept(MediaType.APPLICATION_JSON)
        val body = client.toBlocking().retrieve(request)
        assertNotNull(body)
    }

    @Test
    fun getProto() {
        val request: HttpRequest<Any> = HttpRequest.GET<Any?>("/scene").accept(ProtoBufferCodec.PROTO_BUFFER)
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