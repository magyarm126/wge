package hu.matemagyar.wge.nes.memory

@OptIn(ExperimentalUnsignedTypes::class)
abstract class AbstractMemory(size: UShort) : Memory {
    var memory: UByteArray = UByteArray(size.toInt())

    override fun readByte(rawAddress: UShort): UByte {
        return memory[rawAddress.toInt()]
    }

    override fun writeByte(
        rawAddress: UShort,
        data: UByte,
    ) {
        memory[rawAddress.toInt()] = data
    }

    override fun getCapacity(): UShort {
        return memory.size.toUShort()
    }
}
