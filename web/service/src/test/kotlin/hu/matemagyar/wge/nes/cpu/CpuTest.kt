package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.cpu.register.StatusRegister
import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Replaces
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

@MicronautTest
class CpuTest {
    @Inject
    lateinit var cpu: Cpu

    @Inject
    lateinit var memoryBus: MemoryBus

    @Singleton
    @Replaces(MemoryBus::class)
    fun mockMemoryBus(): MemoryBus {
        return mock(MemoryBus::class.java)
    }

    @BeforeEach
    fun setup() {
        reset(memoryBus)
    }

    @Test
    fun `adc throws exception if address is null`() {
        assertThrows<NullPointerException> {
            cpu.adc(CpuContext(null, AddressingMode.ZERO_PAGE))
        }
    }

    @Test
    fun `adc correctly adds with carry set`() {
        cpu.accumulator.data = 100u
        cpu.statusRegister.assign(StatusRegister.StatusFlags.CARRY, true)
        whenever(memoryBus.readByte(0x10u)).thenReturn(50u)

        cpu.adc(CpuContext(0x10u, AddressingMode.ZERO_PAGE))

        val expected = (100u + 1u + 50u).toUByte() // carry is 1
        expectThat(cpu.accumulator.data).isEqualTo(expected)
        expectThat(cpu.statusRegister.getFlagValue(StatusRegister.StatusFlags.CARRY)).isFalse()
        expectThat(cpu.statusRegister.getFlagValue(StatusRegister.StatusFlags.ZERO)).isFalse()
    }

    @Test
    fun `adc sets carry flag on overflow`() {
        cpu.accumulator.data = 255u
        cpu.statusRegister.assign(StatusRegister.StatusFlags.CARRY, false)
        whenever(memoryBus.readByte(0x20u)).thenReturn(1u)

        cpu.adc(CpuContext(0x20u, AddressingMode.ZERO_PAGE))

        expectThat(cpu.accumulator.data).isEqualTo(0u)
        expectThat(cpu.statusRegister.getFlagValue(StatusRegister.StatusFlags.CARRY)).isTrue()
    }

    @Test
    fun `adc sets zero flag when result is zero`() {
        cpu.accumulator.data = 0u
        cpu.statusRegister.assign(StatusRegister.StatusFlags.CARRY, false)
        whenever(memoryBus.readByte(0x30u)).thenReturn(0u)

        cpu.adc(CpuContext(0x30u, AddressingMode.ZERO_PAGE))

        expectThat(cpu.accumulator.data).isEqualTo(0u)
        expectThat(cpu.statusRegister.getFlagValue(StatusRegister.StatusFlags.ZERO)).isTrue()
    }

    @Test
    fun `adc sets negative flag when result has high bit set`() {
        cpu.accumulator.data = 0x40u
        cpu.statusRegister.assign(StatusRegister.StatusFlags.CARRY, false)
        whenever(memoryBus.readByte(0x40u)).thenReturn(0x40u)

        cpu.adc(CpuContext(0x40u, AddressingMode.ZERO_PAGE))

        expectThat(cpu.statusRegister.getFlagValue(StatusRegister.StatusFlags.NEGATIVE)).isTrue()
    }

    @Test
    fun `getAddress returns null for ACCUMULATOR mode`() {
        cpu.programCounter.data = 0x1000u
        val address = cpu.getAddress(AddressingMode.ACCUMULATOR)
        expectThat(address).isNull()
    }

    @Test
    fun `getAddress calculates immediate address correctly`() {
        cpu.programCounter.data = 0x2000u
        val address = cpu.getAddress(AddressingMode.IMMEDIATE)
        expectThat(address).isEqualTo(0x2001u)
    }

    @Test
    fun `getAddress calculates zero page address correctly`() {
        cpu.programCounter.data = 0x3000u
        whenever(memoryBus.readByte(0x3001u)).thenReturn(0x80u)
        val address = cpu.getAddress(AddressingMode.ZERO_PAGE)
        expectThat(address).isEqualTo(0x80u)
    }

    @Test
    fun `getAddress calculates zero page X with wrapping`() {
        cpu.programCounter.data = 0x4000u
        cpu.indX.data = 0xFFu
        whenever(memoryBus.readByte(0x4001u)).thenReturn(0x02u)
        val address = cpu.getAddress(AddressingMode.ZERO_PAGE_X)
        expectThat(address).isEqualTo(0x01u) // (2 + 255) & 0xFF = 1
    }

    @Test
    fun `getAddress calculates absolute address`() {
        cpu.programCounter.data = 0x5000u
        whenever(memoryBus.read16Bit(0x5001u)).thenReturn(0x1234u)
        val address = cpu.getAddress(AddressingMode.ABSOLUTE)
        expectThat(address).isEqualTo(0x1234u)
    }

    @Test
    fun `getAddress handles indirect JMP bug`() {
        cpu.programCounter.data = 0x6000u
        val pointer = 0x30FFu
        whenever(memoryBus.read16Bit(0x6001u)).thenReturn(pointer.toUShort())
        whenever(memoryBus.readByte(pointer.toUShort())).thenReturn(0xAAu)
        whenever(memoryBus.readByte(0x3000u)).thenReturn(0xBBu)

        val address = cpu.getAddress(AddressingMode.INDIRECT)

        expectThat(address).isEqualTo(0xBBAAu)
    }

    @Test
    fun `cpuStep invokes opcode function`() {
        cpu.programCounter.data = 0x1000u
        val opcode = 0x01u
        whenever(memoryBus.readByte(any())).thenReturn(opcode.toUByte()) // operand read
        whenever(memoryBus.readByte(cpu.programCounter.data)).thenReturn(opcode.toUByte())

        var invoked = false
        cpu.instructions[opcode.toInt()] = { context ->
            invoked = true
            expectThat(AddressingMode.fromNumber(cpu.addressingModes[opcode.toInt()])).isEqualTo(context.addressingMode)
            assert(context.address != null)
        }

        cpu.cpuStep()
        expectThat(invoked).isTrue()
    }
}
