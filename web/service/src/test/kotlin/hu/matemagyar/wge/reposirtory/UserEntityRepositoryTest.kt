package hu.matemagyar.wge.reposirtory

import hu.matemagyar.wge.repository.UserRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

@MicronautTest
class UserEntityRepositoryTest {

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun userRepositoryIntegrationTest() {
        userRepository.findAll()
    }
}