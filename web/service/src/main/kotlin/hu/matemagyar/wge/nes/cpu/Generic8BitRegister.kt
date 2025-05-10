package hu.matemagyar.wge.nes.cpu

import io.micronaut.context.annotation.Prototype
import java.util.function.Supplier

@Prototype
class Generic8BitRegister : Cpu8BitRegister<Generic8BitRegister.GenericBits>() {
    enum class GenericBits(val byteLocation: UByte) : Supplier<UByte> {
        BIT0(0u),
        BIT1(1u),
        BIT2(2u),
        BIT3(3u),
        BIT4(4u),
        BIT5(5u),
        BIT6(6u),
        BIT7(7u), ;

        override fun get(): UByte {
            return this.byteLocation
        }
    }
}
