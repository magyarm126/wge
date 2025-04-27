package hu.matemagyar.wge.nes.memory

import jakarta.inject.Singleton

@Singleton
class PpuRam : AbstractMemory(0x8)
