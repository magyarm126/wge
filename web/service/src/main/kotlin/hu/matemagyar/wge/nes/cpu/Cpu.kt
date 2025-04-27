package hu.matemagyar.wge.nes.cpu

import hu.matemagyar.wge.nes.memory.MemoryBus
import jakarta.inject.Inject

class Cpu {
    @Inject
    lateinit var memoryBus: MemoryBus
}