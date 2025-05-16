package hu.matemagyar.wge.nes.memory

import hu.matemagyar.wge.toFormattedHexString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import strikt.api.expect
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure

@ExtendWith(MockitoExtension::class)
class MemoryBusTest {
    @InjectMocks lateinit var toBeTested: MemoryBusImpl

    @Mock lateinit var cpuRamMock: CpuRam

    @Mock lateinit var ppuRamMock: PpuRam

    @Mock lateinit var apuRamMock: ApuRam

    @Test
    fun writeReadCpuRam() {
        for (address in 0u..<0x2000u) {
            toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte())
            toBeTested.readByte(address.toUShort())
        }
        Mockito.verify(cpuRamMock, Mockito.times(0x2000))
            .readByte(any())
        Mockito.verify(cpuRamMock, Mockito.times(0x2000))
            .writeByte(any(), any())
        Mockito.verifyNoInteractions(ppuRamMock, apuRamMock)
    }

    @Test
    fun writeReadPpuRam() {
        for (address in 0x2000u..<0x4000u) {
            toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte())
            toBeTested.readByte(address.toUShort())
        }
        Mockito.verify(ppuRamMock, Mockito.times(0x2000))
            .readByte(any())
        Mockito.verify(ppuRamMock, Mockito.times(0x2000))
            .writeByte(any(), any())
        Mockito.verifyNoInteractions(cpuRamMock, apuRamMock)
    }

    @Test
    fun writeReadApuRam() {
        for (address in 0x4000u..<0x4018u) {
            toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte())
            toBeTested.readByte(address.toUShort())
        }
        Mockito.verify(apuRamMock, Mockito.times(0x18))
            .readByte(any())
        Mockito.verify(apuRamMock, Mockito.times(0x18))
            .writeByte(any(), any())
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun writeReadApuRamDisabledFunctionality() {
        for (address in 0x4018u..<0x4020u) {
            expect {
                catching { toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte()) }.describedAs(
                    "APU and I/O functionality that is normally disabled, " +
                        "write should throw error at address ${address.toUShort().toFormattedHexString()}",
                ).isFailure().isA<NotImplementedError>()
                catching { toBeTested.readByte(address.toUShort()) }.describedAs(
                    "APU and I/O functionality that is normally disabled, " +
                        "read should throw error  at address ${address.toUShort().toFormattedHexString()}",
                ).isFailure().isA<NotImplementedError>()
            }
        }
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun writeReadUnmappedFunctionality() {
        for (address in 0x4020u..<0x8000u) {
            expect {
                catching { toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte()) }.describedAs(
                    "Unmapped, write should throw error at address ${address.toUShort().toFormattedHexString()}",
                ).isFailure().isA<NotImplementedError>()
                catching { toBeTested.readByte(address.toUShort()) }.describedAs(
                    "Unmapped, read should throw error  at address ${address.toUShort().toFormattedHexString()}",
                ).isFailure().isA<NotImplementedError>()
            }
        }
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @Test
    fun getCapacity() {
        expectThat(toBeTested.getCapacity()).isEqualTo(0x8000u)
        Mockito.verifyNoInteractions(cpuRamMock, ppuRamMock, apuRamMock)
    }

    @Test
    fun oneOverCapacityAddressShouldThrowException() {
        expectThrows<IndexOutOfBoundsException> { toBeTested.writeByte(0x8000u, 0x7u) }
    }
}
