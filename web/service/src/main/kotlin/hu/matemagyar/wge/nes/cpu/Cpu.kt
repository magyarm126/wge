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
        address: UShort?,
        addressingMode: AddressingMode,
    ) {
        val memory = memoryBus.readByte(address!!)
        val result: UByte = (accumulator.data + statusRegister.getFlagValueAsNumber(StatusRegister.StatusFlags.CARRY) + memory).toUByte()
        statusRegister.assign(StatusRegister.StatusFlags.CARRY, result > UByte.MAX_VALUE)
        statusRegister.assign(StatusRegister.StatusFlags.ZERO, 0u.equals(result))
        statusRegister.assign(StatusRegister.StatusFlags.NEGATIVE, result and 0b010000000u.toUByte())
        statusRegister.assign(StatusRegister.StatusFlags.OVERFLOW, (result xor accumulator.data) and (result xor memory) and 0x80u)

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

    /**
     * Resolves the effective memory address for the specified 6502 addressing mode.
     *
     * Note: The PC points to the opcode byte. Operand bytes follow immediately after.
     *
     * @param addressMode The addressing mode used by the instruction.
     * @return The resolved memory address, or `null` if the mode doesn't use a memory address (e.g. ACCUMULATOR).
     */
    fun getAddress(addressMode: AddressingMode): UShort? {
        val pc = programCounter.data

        return when (addressMode) {
            /**
             * IMMEDIATE (#$nn): 1 operand byte follows opcode.
             * Operand is the next byte itself, not a memory address.
             */
            AddressingMode.IMMEDIATE ->
                (pc + 1u).toUShort() // operand at PC+1

            /**
             * ZERO PAGE ($nn): 1 operand byte.
             * Operand byte is an 8-bit zero page address.
             */
            AddressingMode.ZERO_PAGE ->
                memoryBus.readByte((pc + 1u).toUShort()).toUShort()

            /**
             * ZERO PAGE,X ($nn,X): 1 operand byte.
             * Add X register to zero page address, wrap at 0xFF.
             */
            AddressingMode.ZERO_PAGE_X ->
                ((memoryBus.readByte((pc + 1u).toUShort()) + indX.data) and 0xFFu).toUShort()

            /**
             * ZERO PAGE,Y ($nn,Y): 1 operand byte.
             * Add Y register to zero page address, wrap at 0xFF.
             */
            AddressingMode.ZERO_PAGE_Y ->
                ((memoryBus.readByte((pc + 1u).toUShort()) + indY.data) and 0xFFu).toUShort()

            /**
             * ABSOLUTE ($nnnn): 2 operand bytes.
             * 16-bit absolute address follows opcode (low byte at PC+1, high byte at PC+2).
             */
            AddressingMode.ABSOLUTE ->
                memoryBus.read16Bit((pc + 1u).toUShort())

            /**
             * ABSOLUTE,X ($nnnn,X): 2 operand bytes.
             * 16-bit base address plus X register.
             */
            AddressingMode.ABSOLUTE_X ->
                (memoryBus.read16Bit((pc + 1u).toUShort()).toInt() + indX.data.toInt()).toUShort()

            /**
             * ABSOLUTE,Y ($nnnn,Y): 2 operand bytes.
             * 16-bit base address plus Y register.
             */
            AddressingMode.ABSOLUTE_Y ->
                (memoryBus.read16Bit((pc + 1u).toUShort()).toInt() + indY.data.toInt()).toUShort()

            /**
             * INDIRECT ($nnnn): 2 operand bytes.
             * Used only by JMP. Reads a 16-bit pointer, then reads target address from that pointer.
             * Emulates 6502 bug when pointer ends at 0xFF.
             */
            AddressingMode.INDIRECT -> {
                val pointer = memoryBus.read16Bit((pc + 1u).toUShort())
                val lo = memoryBus.readByte(pointer)
                val hi =
                    if (pointer.toUByte() == 0xFFu.toUByte()) {
                        memoryBus.readByte((pointer and 0xFF00u))
                    } else {
                        memoryBus.readByte((pointer + 1u).toUShort())
                    }
                ((hi.toInt() shl 8) or lo.toInt()).toUShort()
            }

            /**
             * INDEXED INDIRECT ( ($nn,X) ): 1 operand byte.
             * Add X register to zero-page operand, then read 16-bit pointer from that address.
             */
            AddressingMode.INDIRECT_X -> {
                val base = memoryBus.readByte((pc + 1u).toUShort())
                val addr = ((base + indX.data) and 0xFFu).toUByte()
                val lo = memoryBus.readByte(addr.toUShort())
                val hi = memoryBus.readByte(((addr + 1u) and 0xFFu).toUShort())
                ((hi.toInt() shl 8) or lo.toInt()).toUShort()
            }

            /**
             * INDIRECT INDEXED ( ($nn),Y ): 1 operand byte.
             * Read 16-bit pointer from zero-page operand, then add Y register.
             */
            AddressingMode.INDIRECT_Y -> {
                val base = memoryBus.readByte((pc + 1u).toUShort())
                val lo = memoryBus.readByte(base.toUShort())
                val hi = memoryBus.readByte(((base + 1u) and 0xFFu).toUShort())
                val addr = ((hi.toInt() shl 8) or lo.toInt()) + indY.data.toInt()
                addr.toUShort()
            }

            /**
             * RELATIVE ($±nn): 1 operand byte.
             * Signed offset for branching, relative to PC+2 (opcode + operand).
             */
            AddressingMode.RELATIVE -> {
                val offset = memoryBus.readByte((pc + 1u).toUShort()).toByte().toInt()
                (pc.toInt() + offset).toUShort()
            }

            /**
             * ACCUMULATOR (A): No operand bytes.
             * Instruction operates directly on the accumulator register.
             */
            AddressingMode.ACCUMULATOR -> null
        }
    }

    var opCodeFunctions: Array<Function2<UShort?, AddressingMode, Unit>> =
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
