package hu.matemagyar.wge.proto.codec

import com.google.protobuf.ExtensionRegistry
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Factory
@Requires(classes = [ExtensionRegistry::class])
class ExtensionRegistryFactory {
    @Singleton
    protected fun extensionRegistry(): ExtensionRegistry {
        return ExtensionRegistry.newInstance()
    }
}