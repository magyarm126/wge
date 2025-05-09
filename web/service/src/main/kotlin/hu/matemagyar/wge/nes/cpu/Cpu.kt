package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.memory.MemoryBus
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject

@Prototype
class Cpu {
    @Inject
    lateinit var memoryBus: MemoryBus

    var cycleCounter: Int = 0

    // ALU
    var a: Byte = 0

    // Indexes
    var x: Byte = 0
    var y: Byte = 0

    // Stack pointer
    var s: Byte = 0

    // Status register
    var p: Byte = 0

    // Program counter
    var pc: Short = 0
}
