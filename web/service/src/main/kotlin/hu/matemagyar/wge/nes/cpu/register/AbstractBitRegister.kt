package hu.matemagyar.wge.nes.cpu.register

import hu.matemagyar.wge.toFormattedHexString
import java.util.function.Supplier

abstract class AbstractBitRegister<T : Supplier<UByte>> {
    var data: UByte = 0u

    fun set(statusFlagProvider: T) {
        data = data.or(getRegisterMask(statusFlagProvider.get()))
    }

    fun assign(
        statusFlagProvider: T,
        shouldSet: Boolean,
    ) {
        if (shouldSet) {
            set(statusFlagProvider)
        } else {
            unset(statusFlagProvider)
        }
    }

    fun getFlagValue(statusFlagProvider: T): Boolean {
        return data.and(getRegisterMask(statusFlagProvider.get())) > 0u
    }

    fun getFlagValueAsNumber(statusFlagProvider: T): UByte {
        return if (getFlagValue(statusFlagProvider)) 1u else 0u
    }

    fun unset(statusFlagProvider: T) {
        data = data.and(getRegisterMask(statusFlagProvider.get()).xor(0b11111111u))
    }

    protected fun getRegisterMask(bitIndex: UByte): UByte {
        val registerMask = bitMaskMap[bitIndex]
        if (registerMask != null) {
            return registerMask
        }
        throw IllegalStateException("Register map doesn't contain mapping for ${bitIndex.toUShort().toFormattedHexString()}")
    }

    private val bitMaskMap: Map<UByte, UByte> =
        mapOf(
            0u.toUByte() to 0b1u,
            1u.toUByte() to 0b10u,
            2u.toUByte() to 0b100u,
            3u.toUByte() to 0b1000u,
            4u.toUByte() to 0b10000u,
            5u.toUByte() to 0b100000u,
            6u.toUByte() to 0b1000000u,
            7u.toUByte() to 0b10000000u,
        )
}
