package hu.matemagyar.wge.service

import hu.matemagyar.wge.entity.UserEntity
import hu.matemagyar.wge.repository.UserRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension


@MicronautTest
@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Test
    fun findAllTest() {
        `when`(userRepository.findAll()).thenReturn(listOf(UserEntity(name = "User1"), UserEntity(name = "User2")))
        val result = userService.getUsers()
        Assertions.assertEquals(2, result.size)
    }
}