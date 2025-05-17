package hu.matemagyar.wge.nes.memory

data class MemoryMapping(val memory: AbstractMemory, val localAddress: UShort) {
    inline fun <R> map(block: (AbstractMemory, UShort) -> R): R = block(memory, localAddress)
}
