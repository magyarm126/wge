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
    private var cpuRam: CpuRam
    private var ppuRam: PpuRam
    private var apuRam: ApuRam

    @Inject
    constructor(cpuRam: CpuRam, ppuRam: PpuRam, apuRam: ApuRam) {
        this.cpuRam = cpuRam
        this.ppuRam = ppuRam
        this.apuRam = apuRam
    }

    override fun readByte(rawAddress: UShort): UByte {
        selectMemoryUnitToAddress(rawAddress).apply { return first.readByte(second) }
    }

    fun read16Bit(rawAddress: UShort): UShort {
        selectMemoryUnitToAddress(rawAddress).apply { return first.readByte(second).toUShort() } // TODO actually implement, this is wrong
    }

    override fun writeByte(
        rawAddress: UShort,
        data: UByte,
    ) {
        selectMemoryUnitToAddress(rawAddress).apply { return first.writeByte(second, data) }
    }

    override fun getCapacity(): UShort {
        return 0x8000u
    }

    fun selectMemoryUnitToAddress(address: UShort): Pair<AbstractMemory, UShort> {
        return when (address) {
            in 0u..<0x800u -> cpuRam to address
            in 0x800u..<0x2000u -> cpuRam to ((address and 0x7FFu) + 0x800u).toUShort()
            in 0x2000u..<0x2008u -> ppuRam to (address and 0b111u)
            in 0x2008u..<0x4000u -> ppuRam to ((address - 0x8u) and 0b111u).toUShort()
            in 0x4000u..<0x4018u -> apuRam to (address and 0x18u)
            in 0x4018u..<0x4020u -> throw NotImplementedError(
                "APU and I/O functionality that is normally disabled. Address:${address.toFormattedHexString()}",
            )
            in 0x4020u..<0x8000u -> throw NotImplementedError("Needs cartridge RAM/ROM implementation")
            else -> throw IndexOutOfBoundsException(
                "Address out of bounds: ${address.toFormattedHexString()}, addressable range: 0-${getCapacity().toFormattedHexString()}",
            )
        }
    }
}
