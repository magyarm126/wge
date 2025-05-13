package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.cpu.register.Generic8BitRegister
import hu.matemagyar.wge.nes.cpu.register.ProgramCounter
import hu.matemagyar.wge.nes.cpu.register.StatusRegister
import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject
import kotlin.reflect.KFunction3

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
        baseCycleCost: Int,
    ) {
        val memory = memoryBus.readByte(address)
        val result: UByte =
            (
                accumulator.data +
                    statusRegister.getFlagValue(
                        StatusRegister.StatusFlags.CARRY,
                    ) + memory
            ).toUByte()
        statusRegister.assign(StatusRegister.StatusFlags.CARRY, result > UByte.MAX_VALUE)
        statusRegister.assign(StatusRegister.StatusFlags.ZERO, 0u.equals(result))
        statusRegister.assign(StatusRegister.StatusFlags.NEGATIVE, result and 0b01000000u.toUByte() == 0b01000000u.toUByte())
        val overflowCalculation = (result xor accumulator.data) and (result xor memory) and 0x80u
        statusRegister.assign(StatusRegister.StatusFlags.NEGATIVE, 0x80u.equals(overflowCalculation))

        // todo: add cycle
        if (addressingMode == AddressingMode.ZERO_PAGE) {
            cycleCounter += baseCycleCost // todo actually implement it
        }
        accumulator.data = result
    }

    fun caller(
        opcode: Int,
        address: UShort,
    ) {
        val x = opcode and 0xF0
        val y = opcode and 0xF
        opCodeFunctions[x][y].invoke(
            address,
            AddressingMode.fromNumber(addressingModes[x][y]),
            cycles[x][y],
        )
    }

    var opCodeFunctions: Array<Array<KFunction3<UShort, AddressingMode, Int, Unit>>> =
        arrayOf(
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
            arrayOf(::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc),
        )

    var addressingModes =
        arrayOf(
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
        )

    var cycles =
        arrayOf(
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7),
        )
}
