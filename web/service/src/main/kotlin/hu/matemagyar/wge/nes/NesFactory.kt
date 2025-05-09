package hu.matemagyar.wge.nes

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject

@Factory
class NesFactory {
    @Inject
    private lateinit var applicationContext: ApplicationContext

    fun createNesInstance(): Nes {
        return applicationContext.getBean(Nes::class.java)
    }
}
