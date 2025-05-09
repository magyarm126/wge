package hu.matemagyar.wge.nes.memory

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PpuRamTest {
    @InjectMocks
    lateinit var toBeTested: PpuRam

    @Test
    fun ramCapacity() {
        Assertions.assertEquals(0x8, toBeTested.getCapacity())
    }

    @Test
    fun writeReadForAllAddressableRange() {
        for (address in 0..<0x8) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
        }

        for (address in 0..<0x8) {
            Assertions.assertEquals((address or 0b111).toByte(), toBeTested.readByte(address))
            toBeTested.writeByte(address, (address or 0b111).toByte())
        }
    }

    @Test
    fun memoryOutOfBounds() {
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
                    { toBeTested.writeByte(0x8, 0x7) },
                    "Capacity + 1 address should throw",
                )
            },
        )
    }
}
