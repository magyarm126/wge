package hu.matemagyar.wge.nes.memory

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

@ExtendWith(MockitoExtension::class)
class ApuRamTest {
    @InjectMocks lateinit var toBeTested: ApuRam

    @Test
    fun ramCapacity() {
        expectThat(toBeTested.getCapacity()).isEqualTo(0x18)
    }

    @Test
    fun writeReadForAllAddressableRange() {
        for (address in 0..<0x18) {
            toBeTested.writeByte(address, (address or 0b111).toByte())
        }

        for (address in 0..<0x18) {
            expectThat(toBeTested.readByte(address)).isEqualTo((address or 0b111).toByte())
            toBeTested.writeByte(address, (address or 0b111).toByte())
        }
    }

    @Test
    fun negativeAddressShouldThrowException() {
        expectThrows<IndexOutOfBoundsException> { toBeTested.writeByte(-0b1, 0x7) }
    }

    @Test
    fun oneOverCapacityAddressShouldThrowException() {
        expectThrows<IndexOutOfBoundsException> { toBeTested.writeByte(0x18, 0x7) }
    }
}
