package hu.matemagyar.wge.proto

import io.micronaut.context.annotation.Primary
import io.micronaut.core.type.Argument
import io.micronaut.http.MediaType
import io.micronaut.http.body.MessageBodyHandlerRegistry
import io.micronaut.http.body.MessageBodyReader
import io.micronaut.http.body.MessageBodyWriter
import io.micronaut.http.netty.body.NettyJsonHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
@Primary
class CustomMessageBodyHandlerRegistry : MessageBodyHandlerRegistry {

    @Inject
    lateinit var protoBufferBodyHandler: ProtoBufferBodyHandler<*>

    @Inject
    lateinit var nettyJsonHandler: NettyJsonHandler<*>

    override fun <T : Any?> findReader(
        type: Argument<T>?,
        mediaType: MutableList<MediaType>?
    ): Optional<MessageBodyReader<T>> {
        if (mediaType!!.stream().anyMatch {
                it.type.equals(ProtoBufferCodec.PROTOBUFFER_ENCODED) || it.type.equals(ProtoBufferCodec.PROTOBUFFER_ENCODED2)
            }) {
            return Optional.of(protoBufferBodyHandler) as Optional<MessageBodyReader<T>>
        }
        return Optional.of(nettyJsonHandler) as Optional<MessageBodyReader<T>>
    }

    override fun <T : Any?> findWriter(
        type: Argument<T>?,
        mediaType: MutableList<MediaType>?
    ): Optional<MessageBodyWriter<T>> {
        if (mediaType!!.stream().anyMatch {
                ProtoBufferCodec.PROTOBUFFER_ENCODED == it.name || ProtoBufferCodec.PROTOBUFFER_ENCODED2 == it.name
            }) {
            return Optional.of(protoBufferBodyHandler) as Optional<MessageBodyWriter<T>>
        }
        return Optional.of(nettyJsonHandler) as Optional<MessageBodyWriter<T>>
    }
}