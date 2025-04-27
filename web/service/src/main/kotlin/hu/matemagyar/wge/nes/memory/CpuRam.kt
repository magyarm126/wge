package hu.matemagyar.wge.nes.memory

import jakarta.inject.Singleton

@Singleton
class CpuRam : AbstractMemory(0x0800)
