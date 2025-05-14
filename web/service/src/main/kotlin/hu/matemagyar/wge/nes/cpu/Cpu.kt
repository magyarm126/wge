package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.cpu.register.Generic8BitRegister
import hu.matemagyar.wge.nes.cpu.register.ProgramCounter
import hu.matemagyar.wge.nes.cpu.register.StatusRegister
import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject
import kotlin.reflect.KFunction2

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
        address: UShort?,
        addressingMode: AddressingMode,
    ) {
        if (address == null) throw NullPointerException("address cannot be null")

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
            cycleCounter++ // todo actually implement it
        }
        accumulator.data = result
    }

    fun cpuStep() {
        val opcode: UByte = memoryBus.readByte(programCounter.data)
        programCounter.data++

        // read some magic to get this
        val addressingMode = AddressingMode.fromNumber(addressingModes[opcode.toInt()])
        val address: UShort? = getAddress(addressingMode)

        opCodeFunctions[opcode.toInt()].invoke(
            address,
            addressingMode,
        )
    }

    fun getAddress(addressMode: AddressingMode): UShort? {
        when (addressMode) {
            AddressingMode.IMMEDIATE -> return (programCounter.data + 1u).toUShort()
            AddressingMode.ZERO_PAGE -> return memoryBus.readByte(((programCounter.data + 1u).toUShort())).toUShort()
            AddressingMode.ZERO_PAGE_X -> return (
                (
                    memoryBus.readByte((programCounter.data + 1u).toUShort()) +
                        indX.data
                ) and 0xFFu
            ).toUShort()
            AddressingMode.ZERO_PAGE_Y -> return (
                (
                    memoryBus.readByte((programCounter.data + 1u).toUShort()) +
                        indY.data
                ) and 0xFFu
            ).toUShort()
            AddressingMode.ABSOLUTE -> return memoryBus.read16Bit(((programCounter.data + 1u).toUShort()))
            AddressingMode.ABSOLUTE_X -> return memoryBus.read16Bit(((programCounter.data + indX.data).toUShort()))
            AddressingMode.ABSOLUTE_Y -> return memoryBus.read16Bit(((programCounter.data + indY.data).toUShort()))
            AddressingMode.INDIRECT -> {
                val pointer = memoryBus.read16Bit((programCounter.data + 1u).toUShort())
                val lo = memoryBus.readByte(pointer)
                val hi =
                    if (pointer.toUByte() == 0xFF.toUByte()) {
                        // Simulate 6502 page wrap bug
                        memoryBus.readByte((pointer and 0xFF00u))
                    } else {
                        memoryBus.readByte((pointer + 1u).toUShort())
                    }
                return ((hi.toInt() shl 8) or lo.toInt()).toUShort()
            }
            AddressingMode.INDIRECT_X -> {
                val base = memoryBus.readByte((programCounter.data + 1u).toUShort())
                val addr = ((base + indX.data) and 0xFFu).toUByte()
                val lo = memoryBus.readByte(addr.toUShort())
                val hi = memoryBus.readByte(((addr + 1u) and 0xFFu).toUShort())
                return ((hi.toInt() shl 8) or lo.toInt()).toUShort()
            }
            AddressingMode.INDIRECT_Y -> {
                val base = memoryBus.readByte((programCounter.data + 1u).toUShort())
                val lo = memoryBus.readByte(base.toUShort())
                val hi = memoryBus.readByte(((base + 1u) and 0xFFu).toUShort())
                val addr = ((hi.toInt() shl 8) or lo.toInt()) + indY.data.toInt()
                return addr.toUShort()
            }
            AddressingMode.RELATIVE -> return (
                programCounter.data.toInt() +
                    (memoryBus.readByte((programCounter.data + 1u).toUShort())).toByte().toInt()
            ).toUShort()
            AddressingMode.ACCUMULATOR -> return null
        }
    }

    var opCodeFunctions: Array<KFunction2<UShort?, AddressingMode, Unit>> =
        arrayOf(
            // _0   0x_1   0x_2   0x_3   0x_4   0x_5   0x_6   0x_7   0x_8   0x_9   0x_a   0x_b   0x_c   0x_d   0x_e   0x_f
            // 0x0_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x1_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x2_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x3_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x4_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x5_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x6_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x7_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x8_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0x9_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xa_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xb_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xc_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xd_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xe_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
            // 0xf_
            ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc, ::adc,
        )

    var addressingModes: Array<Int> =
        arrayOf(
            // 1 _2 _3 _4 _5 _6 _7 _8 _9 _a _b _c _d _e _f
            // 0x0_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x1_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x2_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x3_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x4_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x5_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x6_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x7_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x8_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x9_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xa_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xb_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xc_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xd_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xe_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xf_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
        )

    var cycles: Array<Int> =
        arrayOf(
            // 1 _2 _3 _4 _5 _6 _7 _8 _9 _a _b _c _d _e _f
            // 0x0_
            0, 3, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x1_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x2_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x3_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x4_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x5_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x6_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x7_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x8_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0x9_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xa_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xb_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xc_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xd_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xe_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
            // 0xf_
            0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
        )
}
