package hu.matemagyar.wge.nes.memory

import hu.matemagyar.wge.toFormattedHexString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MemoryBusTest {
    @InjectMocks lateinit var toBeTested: MemoryBus

    @Mock lateinit var cpuRamMock: CpuRam

    @Mock lateinit var ppuRamMock: PpuRam

    @Mock lateinit var apuRamMock: ApuRam

    @Test
    fun writeReadCpuRam() {
        for (address in 0..<0x2000) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
            toBeTested.readByte(address)
        }
        Mockito.verify(cpuRamMock, Mockito.times(0x2000))
            .readByte(Mockito.anyInt())
        Mockito.verify(cpuRamMock, Mockito.times(0x2000))
            .writeByte(Mockito.anyInt(), Mockito.anyByte())
        Mockito.verifyNoInteractions(ppuRamMock, apuRamMock)
    }

    @Test
    fun writeReadPpuRam() {
        for (address in 0x2000..<0x4000) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
            toBeTested.readByte(address)
        }
        Mockito.verify(ppuRamMock, Mockito.times(0x2000))
            .readByte(Mockito.anyInt())
        Mockito.verify(ppuRamMock, Mockito.times(0x2000))
            .writeByte(Mockito.anyInt(), Mockito.anyByte())
        Mockito.verifyNoInteractions(cpuRamMock, apuRamMock)
    }

    @Test
    fun writeReadApuRam() {
        for (address in 0x4000..<0x4018) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
            toBeTested.readByte(address)
        }
        Mockito.verify(apuRamMock, Mockito.times(0x18))
            .readByte(Mockito.anyInt())
        Mockito.verify(apuRamMock, Mockito.times(0x18))
            .writeByte(Mockito.anyInt(), Mockito.anyByte())
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun writeReadApuRamDisabledFunctionality() {
        for (address in 0x4018..<0x4020) {
            Assertions.assertThrows(
                NotImplementedError::class.java,
                { toBeTested.writeByte(address, (address or 0b111).toByte()) },
                "APU and I/O functionality that is normally disabled, " +
                    "write should throw error at address ${address.toFormattedHexString()}",
            )
            Assertions.assertThrows(
                NotImplementedError::class.java,
                { toBeTested.readByte(address) },
                "APU and I/O functionality that is normally disabled, " +
                    "read should throw error  at address ${address.toFormattedHexString()}",
            )
        }
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun writeReadUnmappedFunctionality() {
        for (address in 0x4020..<0x8000) {
            Assertions.assertThrows(
                NotImplementedError::class.java,
                { toBeTested.writeByte(address, (address or 0b111).toByte()) },
                "Unmapped, write should throw error at address ${address.toFormattedHexString()}",
            )
            Assertions.assertThrows(
                NotImplementedError::class.java,
                { toBeTested.readByte(address) },
                "Unmapped, read should throw error  at address ${address.toFormattedHexString()}",
            )
        }
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @Test
    fun getCapacity() {
        Assertions.assertEquals(0x8000, toBeTested.getCapacity())
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @Test
    fun selectMemoryUnitToAddress() {
        Assertions.assertAll(
            "Out of bounds",
            {
                Assertions.assertThrows(
                    IndexOutOfBoundsException::class.java,
                    { toBeTested.writeByte(-0b1, 0x7) },
                    "Negative address should throw",
                )
            },
            {
                Assertions.assertThrows(
                    IndexOutOfBoundsException::class.java,
                    { toBeTested.writeByte(0x8000, 0x7) },
                    "Capacity + 1 address should throw",
                )
            },
        )
    }
}
