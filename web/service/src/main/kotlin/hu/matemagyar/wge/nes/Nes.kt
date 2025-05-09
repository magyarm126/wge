package hu.matemagyar.wge.nes

import hu.matemagyar.wge.nes.cpu.Cpu
import io.micronaut.context.annotation.Prototype
import jakarta.inject.Inject

@Prototype
class Nes {
    @Inject
    lateinit var cpu: Cpu
}
