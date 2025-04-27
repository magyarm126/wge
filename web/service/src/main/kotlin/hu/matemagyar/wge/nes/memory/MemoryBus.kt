package hu.matemagyar.wge.nes.memory

import hu.matemagyar.wge.toFormattedHexString
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.lang.IndexOutOfBoundsException

/**
 * @see <a href="https://www.nesdev.org/wiki/CPU_memory_map">CPU Memory Map</a>
 */
@Singleton
class MemoryBus : Memory {
    private var cpuRam: CpuRam
    private var ppuRam: PpuRam
    private var apuRam: ApuRam

    @Inject
    constructor(cpuRam: CpuRam, ppuRam: PpuRam, apuRam: ApuRam) {
        this.cpuRam = cpuRam
        this.ppuRam = ppuRam
        this.apuRam = apuRam
    }

    override fun readByte(address: Int): Byte {
        selectMemoryUnitToAddress(address).apply { return first.readByte(second) }
    }

    override fun writeByte(
        address: Int,
        data: Byte,
    ) {
        selectMemoryUnitToAddress(address).apply { return first.writeByte(second, data) }
    }

    override fun getCapacity(): Int {
        return 0x8000
    }

    fun selectMemoryUnitToAddress(address: Int): Pair<AbstractMemory, Int> {
        return when (address) {
            in 0..<0x800 -> cpuRam to address
            in 0x800..<0x2000 -> cpuRam to ((address and 0x7FF) + 0x800)
            in 0x2000..<0x2008 -> ppuRam to (address and 0b111)
            in 0x2008..<0x4000 -> ppuRam to ((address - 0x8) and 0b111)
            in 0x4000..<0x4018 -> apuRam to (address and 0x18)
            in 0x4018..<0x4020 -> throw NotImplementedError(
                "APU and I/O functionality that is normally disabled. Address:${address.toFormattedHexString()}",
            )
            in 0x4020..<0x8000 -> throw NotImplementedError("Needs cartridge RAM/ROM implementation")
            else -> throw IndexOutOfBoundsException(
                "Address out of bounds: ${address.toFormattedHexString()}, addressable range: 0-${getCapacity().toFormattedHexString()}",
            )
        }
    }
}
