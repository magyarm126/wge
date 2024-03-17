package hu.matemagyar.wge

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@MicronautTest
class ManualIntegrationTest {

    @Test
    @Disabled
    fun getCurrentUser() {
        while (true) {
            Thread.sleep(100)
        }
    }
}