package hu.matemagyar.wge.nes.cpu

abstract class Memory(size: Int) : IMemory {
    var memory: ByteArray = ByteArray(size)

    override fun readByte(rawAddress: Int): Byte {
        return memory[rawAddress]
    }

    override fun writeByte(rawAddress: Int, data: Byte) {
        memory[rawAddress] = data
    }

    override fun getCapacity(): Int {
        return memory.size
    }
}