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
    lateinit var accumulator: Generic8BitRegister

    @Inject
    lateinit var indX: Generic8BitRegister

    @Inject
    lateinit var indY: Generic8BitRegister

    @Inject
    lateinit var stackPointer: Generic8BitRegister

    @Inject
    lateinit var statusRegister: StatusRegister

    @Inject
    lateinit var programCounter: ProgramCounter

    var cycleCounter: Int = 0

    fun adc(
        address: UShort,
        addressingMode: AddressingMode,
    ) {
        val result: UInt =
            accumulator.data +
                statusRegister.getFlagValue(
                    StatusRegister.StatusFlags.CARRY,
                ) + memoryBus.readByte(address)
        statusRegister.assign(StatusRegister.StatusFlags.CARRY, result > UByte.MAX_VALUE)
        statusRegister.assign(StatusRegister.StatusFlags.ZERO, result == 0u)
        statusRegister.assign(StatusRegister.StatusFlags.NEGATIVE, result or 0b01000000u == 0b01000000u)

        // todo: OVERFLOW
        // todo: add cycle
        if (addressingMode == AddressingMode.ZERO_PAGE) {
            cycleCounter++ // todo actually implement it
        }
        accumulator.data = result.toUByte()
    }
}
