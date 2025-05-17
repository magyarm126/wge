package hu.matemagyar.wge.nes.memory

import hu.matemagyar.wge.toFormattedHexString
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject
import java.lang.IndexOutOfBoundsException

/**
 * @see <a href="https://www.nesdev.org/wiki/CPU_memory_map">CPU Memory Map</a>
 */
@Prototype
class MemoryBus : Memory {
    @Inject
    private lateinit var cpuRam: CpuRam

    @Inject
    private lateinit var ppuRam: PpuRam

    @Inject
    private lateinit var apuRam: ApuRam

    override fun readByte(rawAddress: UShort): UByte {
        return getMemoryMapping(rawAddress).map { mem, addr -> mem.readByte(addr) }
    }

    override fun read16Bit(rawAddress: UShort): UShort {
        val lo = readByte(rawAddress)
        val hi = readByte((rawAddress + 1u).toUShort())
        return ((hi.toInt() shl 8) or lo.toInt()).toUShort()
    }

    override fun writeByte(
        rawAddress: UShort,
        data: UByte,
    ) {
        getMemoryMapping(rawAddress).map { mem, addr -> mem.writeByte(addr, data) }
    }

    override fun getCapacity(): UShort {
        return 0x8000u
    }

    fun getMemoryMapping(address: UShort): MemoryMapping {
        return when (address) {
            in 0u..<0x800u -> MemoryMapping(cpuRam, address)
            in 0x800u..<0x2000u -> MemoryMapping(cpuRam, ((address and 0x7FFu) + 0x800u).toUShort())
            in 0x2000u..<0x2008u -> MemoryMapping(ppuRam, (address and 0b111u))
            in 0x2008u..<0x4000u -> MemoryMapping(ppuRam, ((address - 0x8u) and 0b111u).toUShort())
            in 0x4000u..<0x4018u -> MemoryMapping(apuRam, (address and 0x18u))
            in 0x4018u..<0x4020u -> throw NotImplementedError("Disabled APU/I/O area: ${address.toFormattedHexString()}")
            in 0x4020u..<0x8000u -> throw NotImplementedError("Cartridge RAM/ROM not implemented")
            else -> throw IndexOutOfBoundsException("Address out of bounds: ${address.toFormattedHexString()}")
        }
    }
}
