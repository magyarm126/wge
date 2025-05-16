package hu.matemagyar.wge.nes.memory

interface Memory {
    fun readByte(rawAddress: UShort): UByte

    fun read16Bit(rawAddress: UShort): UShort

    fun writeByte(
        rawAddress: UShort,
        data: UByte,
    )

    fun getCapacity(): UShort
}
