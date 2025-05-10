package hu.matemagyar.wge.nes.memory

interface Memory {
    fun readByte(rawAddress: UShort): UByte

    fun writeByte(
        rawAddress: UShort,
        data: UByte,
    )

    fun getCapacity(): UShort
}
