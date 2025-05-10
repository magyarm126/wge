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
        expectThat(toBeTested.getCapacity()).isEqualTo(0x18u)
    }

    @Test
    fun writeReadForAllAddressableRange() {
        for (address in 0u..<0x18u) {
            toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte())
        }

        for (address in 0u..<0x18u) {
            expectThat(toBeTested.readByte(address.toUShort())).isEqualTo((address or 0b111u).toUByte())
            toBeTested.writeByte(address.toUShort(), (address or 0b111u).toUByte())
        }
    }

    @Test
    fun oneOverCapacityAddressShouldThrowException() {
        expectThrows<IndexOutOfBoundsException> { toBeTested.writeByte(0x18u, 0x7u) }
    }
}
