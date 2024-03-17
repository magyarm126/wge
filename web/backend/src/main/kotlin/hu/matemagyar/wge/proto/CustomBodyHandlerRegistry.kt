package hu.matemagyar.wge.proto

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.http.body.MessageBodyHandlerRegistry
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class CustomBodyHandlerRegistry {

    @Inject
    lateinit var customMessageBodyHandlerRegistry: CustomMessageBodyHandlerRegistry


    @Singleton
    @Primary
    fun getBodyHandler(): MessageBodyHandlerRegistry {
        return customMessageBodyHandlerRegistry
    }
}

