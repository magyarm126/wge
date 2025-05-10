package hu.matemagyar.wge.nes.cpu.register

import hu.matemagyar.wge.toFormattedHexString
import io.micronaut.context.annotation.Prototype
import java.util.function.Supplier

@Prototype
class ProgramCounter {
    var data: UShort = 0u

    fun set(statusFlagProvider: ProgramCounterBits) {
        data = data.or(getRegisterMask(statusFlagProvider.get()))
    }

    fun assign(
        statusFlagProvider: ProgramCounterBits,
        shouldSet: Boolean,
    ) {
        if (shouldSet) {
            set(statusFlagProvider)
        } else {
            unset(statusFlagProvider)
        }
    }

    fun getFlagValue(statusFlagProvider: ProgramCounterBits): UShort {
        return data.and(getRegisterMask(statusFlagProvider.get()))
    }

    fun unset(statusFlagProvider: ProgramCounterBits) {
        data = data.and(getRegisterMask(statusFlagProvider.get()).xor(0b1111111111111111u))
    }

    protected fun getRegisterMask(bitIndex: UShort): UShort {
        val registerMask = bitMaskMap[bitIndex]
        if (registerMask != null) {
            return registerMask
        }
        throw IllegalStateException("Register map doesn't contain mapping for ${bitIndex.toFormattedHexString()}")
    }

    private val bitMaskMap: Map<UShort, UShort> =
        mapOf(
            0u.toUShort() to 0b1u,
            1u.toUShort() to 0b10u,
            2u.toUShort() to 0b100u,
            3u.toUShort() to 0b1000u,
            4u.toUShort() to 0b10000u,
            5u.toUShort() to 0b100000u,
            6u.toUShort() to 0b1000000u,
            7u.toUShort() to 0b10000000u,
            8u.toUShort() to 0b100000000u,
            9u.toUShort() to 0b1000000000u,
            10u.toUShort() to 0b10000000000u,
            11u.toUShort() to 0b100000000000u,
            12u.toUShort() to 0b1000000000000u,
            13u.toUShort() to 0b10000000000000u,
            14u.toUShort() to 0b100000000000000u,
            15u.toUShort() to 0b1000000000000000u,
        )

    enum class ProgramCounterBits(val byteLocation: UShort) : Supplier<UShort> {
        BIT0(0u),
        BIT1(1u),
        BIT2(2u),
        BIT3(3u),
        BIT4(4u),
        BIT5(5u),
        BIT6(6u),
        BIT7(7u),
        BIT8(8u),
        BIT9(9u),
        BIT10(10u),
        BIT11(11u),
        BIT12(12u),
        BIT13(13u),
        BIT14(14u),
        BIT15(15u),
        ;

        override fun get(): UShort {
            return this.byteLocation
        }
    }
}
