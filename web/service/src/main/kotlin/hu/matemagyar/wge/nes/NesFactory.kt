package hu.matemagyar.wge.nes

import io.micronaut.context.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class NesFactory {
    @Inject
    private lateinit var applicationContext: ApplicationContext

    fun createNesInstance(): Nes {
        return applicationContext.getBean(Nes::class.java)
    }
}
