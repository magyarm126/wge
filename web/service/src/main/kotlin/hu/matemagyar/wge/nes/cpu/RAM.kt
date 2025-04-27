package hu.matemagyar.wge.nes.cpu

import jakarta.inject.Singleton

@Singleton
class RAM : Memory(0x0800)