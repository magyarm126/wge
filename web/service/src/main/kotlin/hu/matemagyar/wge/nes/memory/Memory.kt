package hu.matemagyar.wge.nes.memory

interface Memory {
    fun readByte(rawAddress: Int): Byte
    fun writeByte(rawAddress: Int, data: Byte)
    fun getCapacity(): Int
}