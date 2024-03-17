package hu.matemagyar.wge.proto

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.core.io.buffer.ByteBufferFactory
import io.micronaut.http.MediaType
import io.micronaut.http.body.ContextlessMessageBodyHandlerRegistry
import io.micronaut.http.body.MessageBodyHandlerRegistry
import io.micronaut.http.body.RawMessageBodyHandler
import io.micronaut.http.netty.body.NettyJsonHandler
import io.micronaut.runtime.ApplicationConfiguration
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton

@Factory
class CustomBodyHandlerRegistry {

    @Inject
    lateinit var applicationConfiguration: ApplicationConfiguration

    @Inject
    lateinit var byteBufferFactory: ByteBufferFactory<*, *>

    @Inject
    @Named("NettyWritableBodyWriter")
    lateinit var rawMessageBodyHandler: RawMessageBodyHandler<*>

    @Inject
    lateinit var nettyJsonHandler: NettyJsonHandler<*>

    @Inject
    lateinit var protoBufferBodyHandler: ProtoBufferBodyHandler<*>


    @Singleton
    @Primary
    fun getBodyHandler(): MessageBodyHandlerRegistry {
        val contextlessMessageBodyHandlerRegistry =
            ContextlessMessageBodyHandlerRegistry(applicationConfiguration, byteBufferFactory, rawMessageBodyHandler)
        contextlessMessageBodyHandlerRegistry.add(MediaType.APPLICATION_JSON_TYPE, nettyJsonHandler)
        contextlessMessageBodyHandlerRegistry.add(ProtoBufferCodec.PROTOBUFFER_ENCODED_TYPE, protoBufferBodyHandler)
        contextlessMessageBodyHandlerRegistry.add(ProtoBufferCodec.PROTOBUFFER_ENCODED_TYPE2, protoBufferBodyHandler)
        return contextlessMessageBodyHandlerRegistry
    }
}

