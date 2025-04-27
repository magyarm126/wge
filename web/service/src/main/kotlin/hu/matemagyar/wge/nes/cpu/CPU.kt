package hu.matemagyar.wge.nes.cpu

import jakarta.inject.Inject

class CPU {
    @Inject
    lateinit var memoryBus: MemoryBus

    fun asd() {
        memoryBus
    }
}