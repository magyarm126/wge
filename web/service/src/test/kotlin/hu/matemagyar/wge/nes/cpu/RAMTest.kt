package hu.matemagyar.wge.nes.cpu

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RAMTest {

    lateinit var toBeTested: RAM

    @BeforeEach
    fun setUp() {
        toBeTested = RAM()
    }

    @Test
    fun ramCapacity() {
        Assertions.assertEquals(0x800, toBeTested.getCapacity())
    }

    @Test
    fun writeReadForAllAddressableRange() {
        for (address in 0..<0x800) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
        }

        for (address in 0..<0x800) {
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
                    { toBeTested.writeByte(0x800, 0x7) },
                    "Capacity + 1 address should throw"
                )
            }
        )
    }
}