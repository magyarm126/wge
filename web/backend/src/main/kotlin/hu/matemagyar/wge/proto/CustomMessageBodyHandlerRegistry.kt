package hu.matemagyar.wge.proto

import com.google.protobuf.Message
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

@Suppress("UNCHECKED_CAST")
@Singleton
@Primary
class CustomMessageBodyHandlerRegistry<T> : MessageBodyHandlerRegistry {

    @Inject
    lateinit var protoBufferBodyHandler: ProtoBufferBodyHandler<T>

    @Inject
    lateinit var nettyJsonHandler: NettyJsonHandler<T>

    override fun <T> findReader(
        type: Argument<T>,
        mediaType: MutableList<MediaType>
    ): Optional<MessageBodyReader<T>> {
        if (Message::class.java.isAssignableFrom(type.type)) {
            return Optional.of(protoBufferBodyHandler as MessageBodyReader<T>)
        }
        return Optional.of<MessageBodyReader<T>>(nettyJsonHandler as MessageBodyReader<T>)
    }

    override fun <T> findWriter(
        type: Argument<T>,
        mediaType: MutableList<MediaType>
    ): Optional<MessageBodyWriter<T>> {
        if (Message::class.java.isAssignableFrom(type.type)) {
            return Optional.of(protoBufferBodyHandler as MessageBodyWriter<T>)
        }
        return Optional.of(nettyJsonHandler as MessageBodyWriter<T>)
    }
}