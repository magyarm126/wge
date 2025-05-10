package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject

@Prototype
class Cpu {
    @Inject
    lateinit var memoryBus: MemoryBus

    var cycleCounter: Int = 0

    // ALU
    var a: UByte = 0u

    // Indexes
    var x: UByte = 0u
    var y: UByte = 0u

    // Stack pointer
    var s: UByte = 0u

    // Status register
    var p: UByte = 0u

    // Program counter
    var pc: UShort = 0u

    fun adc(
        address: UShort,
        addressingMode: AddressingMode,
    ) {
        val result: UInt = a + StatusRegisterBits.CARRY.bitmask + memoryBus.readByte(address)
        p = StatusRegisterBits.CARRY.set(p, result > UByte.MAX_VALUE)
        p = StatusRegisterBits.ZERO.set(p, result == 0u)
        p = StatusRegisterBits.NEGATIVE.set(p, result or 0b01000000u == 0b01000000u)
        // todo: OVERFLOW
        // todo: add cycle
        if (addressingMode == AddressingMode.ZERO_PAGE) {
            cycleCounter++ // todo actually implement it
        }
        a = result.toUByte()
    }

    enum class AddressingMode {
        IMMEDIATE,
        ZERO_PAGE,
        ZERO_PAGE_X,
        ABSOLUTE,
        ABSOLUTE_X,
        ABSOLUTE_Y,
        INDIRECT_X,
        INDIRECT_Y,
    }

    enum class StatusRegisterBits(val bitmask: UByte) {
        CARRY(0b1u),
        ZERO(0b10u),
        INTERRUPT_DISABLE(0b100u),
        DECIMAL(0b1000u),
        B(0b10000u),
        ONE(0b100000u),
        OVERFLOW(0b1000000u),
        NEGATIVE(0b10000000u),
        ;

        fun set(
            input: UByte,
            shouldSet: Boolean,
        ): UByte {
            return if (shouldSet) {
                set(input)
            } else {
                unset(input)
            }
        }

        fun set(input: UByte): UByte {
            return input.or(bitmask)
        }

        fun unset(input: UByte): UByte {
            return input.and(bitmask.xor(0b11111111u))
        }
    }
}
