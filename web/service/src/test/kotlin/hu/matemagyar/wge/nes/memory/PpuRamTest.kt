package hu.matemagyar.wge.nes.memory

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PpuRamTest {

    lateinit var toBeTested: PpuRam

    @BeforeEach
    fun setUp() {
        toBeTested = PpuRam()
    }

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
                    "Negative address should throw"
                )
            },
            {
                Assertions.assertThrows(
                    IndexOutOfBoundsException::class.java,
                    { toBeTested.writeByte(0x8, 0x7) },
                    "Capacity + 1 address should throw"
                )
            }
        )
    }
}