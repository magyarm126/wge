package hu.matemagyar.wge.nes.memory

import io.micronaut.context.annotation.Prototype

@Prototype
class CpuRam : AbstractMemory(0x0800u)
