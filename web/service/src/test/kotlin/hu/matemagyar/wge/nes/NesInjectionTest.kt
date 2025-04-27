package hu.matemagyar.wge.nes

import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class NesInjectionTest {

    @Inject
    lateinit var memoryBus: MemoryBus

    @Test
    fun micronautDependencyInjection() {
        memoryBus.writeByte(0,2)
        Assertions.assertEquals(2, memoryBus.readByte(0))
    }

}