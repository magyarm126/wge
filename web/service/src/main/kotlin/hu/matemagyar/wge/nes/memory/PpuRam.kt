package hu.matemagyar.wge.nes.memory

import io.micronaut.context.annotation.Prototype

@Prototype
class PpuRam : AbstractMemory(0x8u)
