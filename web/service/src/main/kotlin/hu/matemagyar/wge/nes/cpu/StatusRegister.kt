package hu.matemagyar.wge.nes.cpu

import io.micronaut.context.annotation.Prototype
import java.util.function.Supplier

@Prototype
class StatusRegister : Cpu8BitRegister<StatusRegister.StatusFlags>() {
    enum class StatusFlags(val byteLocation: UByte) : Supplier<UByte> {
        CARRY(0u),
        ZERO(1u),
        INTERRUPT_DISABLE(2u),
        DECIMAL(3u),
        B(4u),
        ONE(5u),
        OVERFLOW(6u),
        NEGATIVE(7u), ;

        override fun get(): UByte {
            return this.byteLocation
        }
    }
}
