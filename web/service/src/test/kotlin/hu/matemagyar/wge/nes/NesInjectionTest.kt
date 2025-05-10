package hu.matemagyar.wge.nes

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@MicronautTest
class NesInjectionTest {
    @Inject
    lateinit var nesFactory: NesFactory

    @Inject
    lateinit var nes: Nes

    @Test
    fun prototypeInjectionTest() {
        val nes2 = nesFactory.createNesInstance()
        val nes3 = nesFactory.createNesInstance()

        nes.cpu.memoryBus.writeByte(0u, 0b001u)
        nes2.cpu.memoryBus.writeByte(0u, 0b010u)
        nes3.cpu.memoryBus.writeByte(0u, 0b100u)

        expectThat(nes.cpu.memoryBus.readByte(0u))
            .isEqualTo(0b001u)
        expectThat(nes2.cpu.memoryBus.readByte(0u))
            .isEqualTo(0b010u)
        expectThat(nes3.cpu.memoryBus.readByte(0u))
            .isEqualTo(0b100u)
    }
}
