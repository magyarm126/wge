package hu.matemagyar.wge.nes.cpu

import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * @see <a href="https://www.nesdev.org/wiki/CPU_memory_map">CPU Memory Map</a>
 */
@Singleton
class MemoryBus: IMemory {
    @Inject
    private lateinit var ram: RAM

    @Inject
    private lateinit var ppuRam: PPU_RAM

    override fun readByte(address: Int) : Byte {
        selectMemoryUnitToAddress(address).apply { return first.readByte(second) }
    }

    override fun writeByte(address: Int, data: Byte){
        selectMemoryUnitToAddress(address).apply { return first.writeByte(second, data) }
    }

    override fun getCapacity(): Int {
        return 0x8000
    }

    // figure this out, this is wrong, write tests https://www.baeldung.com/kotlin/bitwise-operators
    fun selectMemoryUnitToAddress(address: Int) : Pair<Memory, Int> {
        return when(address) {
            in 0..<0x800 -> ram to address
            in 0x800..<0x2000 -> ram to ((address and 0x7FF) + 0x800)
            /*
            0x0800 0000100000000000
            0x2000 0010000000000000

            0x07FF 0000011111111111 AND [0..0x7FF]
            0x0800 0000100000000000 + 0x800
             */
            in 0x2000..<0x2008 -> ram to (address and 0b111)
            /*
            0x2000 0010000000000000
            0x2007 0010000000000111

            0x0007 0010000000000111 AND
             */
            else -> throw RuntimeException("Address out of bounds: $address, max capacity: ${getCapacity()}")
        }
    }
}