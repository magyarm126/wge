package hu.matemagyar.wge.nes.cpu

interface IMemory {
    fun readByte(rawAddress: Int): Byte
    fun writeByte(rawAddress: Int, data: Byte)
    fun getCapacity(): Int
}