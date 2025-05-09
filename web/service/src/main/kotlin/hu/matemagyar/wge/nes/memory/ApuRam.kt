package hu.matemagyar.wge.nes.memory

import io.micronaut.context.annotation.Prototype

@Prototype
class ApuRam : AbstractMemory(0x18)
