package hu.matemagyar.wge.service

import hu.matemagyar.wge.entity.UserEntity
import hu.matemagyar.wge.repository.UserRepository
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@MicronautTest
class UserServiceTest {

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun findAllTest() {
        `when`(userRepository.findAll()).thenReturn(listOf(UserEntity(name = "User1"), UserEntity(name = "User2")))
        val result = userService.getUsers()
        Assertions.assertEquals(2, result.size)
    }

    @MockBean(UserRepository::class)
    fun userRepository(): UserRepository {
        return mock(UserRepository::class.java)
    }
}