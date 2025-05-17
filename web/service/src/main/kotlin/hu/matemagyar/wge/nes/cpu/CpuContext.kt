package hu.matemagyar.wge.nes.cpu

typealias Instruction = (CpuContext) -> Unit

data class CpuContext(
    val address: UShort?,
    val addressingMode: AddressingMode,
)
