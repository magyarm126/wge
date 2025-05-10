package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.cpu.register.Generic8BitRegister
import hu.matemagyar.wge.nes.cpu.register.ProgramCounter
import hu.matemagyar.wge.nes.cpu.register.StatusRegister
import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject

@Prototype
class Cpu {
    @Inject
    lateinit var memoryBus: MemoryBus

    @Inject
    lateinit var a: Generic8BitRegister

    @Inject
    lateinit var x: Generic8BitRegister

    @Inject
    lateinit var y: Generic8BitRegister

    @Inject
    lateinit var s: Generic8BitRegister

    @Inject
    lateinit var p: StatusRegister

    @Inject
    lateinit var pc: ProgramCounter

    var cycleCounter: Int = 0

    fun adc(
        address: UShort,
        addressingMode: AddressingMode,
    ) {
        val result: UInt =
            a.data +
                p.getFlagValue(
                    StatusRegister.StatusFlags.CARRY,
                ) + memoryBus.readByte(address)
        p.assign(StatusRegister.StatusFlags.CARRY, result > UByte.MAX_VALUE)
        p.assign(StatusRegister.StatusFlags.ZERO, result == 0u)
        p.assign(StatusRegister.StatusFlags.NEGATIVE, result or 0b01000000u == 0b01000000u)

        // todo: OVERFLOW
        // todo: add cycle
        if (addressingMode == AddressingMode.ZERO_PAGE) {
            cycleCounter++ // todo actually implement it
        }
        a.data = result.toUByte()
    }
}
