package hu.matemagyar.wge.nes.cpu

enum class AddressingMode(
    /**
     * Number of operand bytes following the opcode for this addressing mode.
     * Used to know how many bytes to read after the opcode and to advance the PC correctly.
     */
    val operandBytes: Int,
) {
    IMMEDIATE(1), // #$nn
    ZERO_PAGE(1), // $nn
    ZERO_PAGE_X(1), // $nn,X
    ZERO_PAGE_Y(1), // $nn,Y
    ABSOLUTE(2), // $nnnn
    ABSOLUTE_X(2), // $nnnn,X
    ABSOLUTE_Y(2), // $nnnn,Y
    INDIRECT(2), // ($nnnn) — JMP only
    INDIRECT_X(1), // ($nn,X)
    INDIRECT_Y(1), // ($nn),Y
    RELATIVE(1), // $±nn (branch offset)
    ACCUMULATOR(0), // A (no operand bytes)
    ;

    companion object {
        fun fromNumber(number: Int): AddressingMode {
            return AddressingMode.entries[number]
        }
    }
}
