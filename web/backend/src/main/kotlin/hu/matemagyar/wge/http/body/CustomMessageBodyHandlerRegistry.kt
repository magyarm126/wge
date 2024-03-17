package hu.matemagyar.wge.http.body

import com.google.protobuf.Message
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Requires
import io.micronaut.core.type.Argument
import io.micronaut.http.MediaType
import io.micronaut.http.body.*
import io.micronaut.http.netty.body.ByteBufRawMessageBodyHandler
import io.micronaut.http.netty.body.NettyJsonHandler
import io.netty.buffer.ByteBuf
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.util.*

@Suppress("UNCHECKED_CAST", "MnInjectionPoints")
@Singleton
@Primary
class CustomMessageBodyHandlerRegistry<T> : MessageBodyHandlerRegistry {

    @Inject
    lateinit var protoBufferBodyHandlerProxy: ProtoBufferBodyHandlerProxy<T>

    @Inject
    lateinit var nettyJsonHandler: NettyJsonHandler<T>

    @Inject
    @Named("ByteBufRawMessageBodyHandler")
    @Requires(classes = [ByteBufRawMessageBodyHandler::class])
    lateinit var byteBufRawMessageBodyHandler: RawMessageBodyHandler<T>

    @Inject
    @Named("RawStringHandler")
    @Requires(classes = [DefaultMessageBodyHandlerRegistry::class])
    lateinit var rawStringHandler: RawMessageBodyHandler<T>

    override fun <T> findReader(
        type: Argument<T>,
        mediaType: MutableList<MediaType>
    ): Optional<MessageBodyReader<T>> {
        if (String::class.java.isAssignableFrom(type.type)) {
            return Optional.of(rawStringHandler as MessageBodyReader<T>)
        }
        if (ByteBuf::class.java.isAssignableFrom(type.type)) {
            return Optional.of(byteBufRawMessageBodyHandler as MessageBodyReader<T>)
        }
        if (Message::class.java.isAssignableFrom(type.type) || type.type.name.equals("java.util.List")) {
            return Optional.of(protoBufferBodyHandlerProxy as MessageBodyReader<T>)
        }
        return Optional.of(nettyJsonHandler as MessageBodyReader<T>)
    }

    override fun <T> findWriter(
        type: Argument<T>,
        mediaType: MutableList<MediaType>
    ): Optional<MessageBodyWriter<T>> {
        if (String::class.java.isAssignableFrom(type.type)) {
            return Optional.of(rawStringHandler as MessageBodyWriter<T>)
        }
        if (ByteBuf::class.java.isAssignableFrom(type.type)) {
            return Optional.of(byteBufRawMessageBodyHandler as MessageBodyWriter<T>)
        }
        if (Message::class.java.isAssignableFrom(type.type) || type.type.name.equals("java.util.List")) {
            return Optional.of(protoBufferBodyHandlerProxy as MessageBodyWriter<T>)
        }
        return Optional.of(nettyJsonHandler as MessageBodyWriter<T>)
    }
}