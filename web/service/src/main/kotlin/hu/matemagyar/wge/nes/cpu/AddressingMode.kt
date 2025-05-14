package hu.matemagyar.wge.nes.cpu

enum class AddressingMode {
    IMMEDIATE,
    ZERO_PAGE,
    ZERO_PAGE_X,
    ZERO_PAGE_Y,
    ABSOLUTE,
    ABSOLUTE_X,
    ABSOLUTE_Y,
    INDIRECT,
    INDIRECT_X,
    INDIRECT_Y,
    RELATIVE,
    ACCUMULATOR,
    ;

    companion object {
        fun fromNumber(number: Int): AddressingMode {
            return AddressingMode.entries[number]
        }
    }
}
