package hu.matemagyar.wge.http.body

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
import hu.matemagyar.wge.http.codec.ProtoBufferCodec
import io.micronaut.core.type.Argument
import io.micronaut.core.type.Headers
import io.micronaut.core.type.MutableHeaders
import io.micronaut.http.HttpHeaders
import io.micronaut.http.MediaType
import io.micronaut.http.body.MessageBodyHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.InputStream
import java.io.OutputStream

@Singleton
class ProtoBufferBodyHandler : MessageBodyHandler<Message> {

    @Inject
    lateinit var codec: ProtoBufferCodec

    @Inject
    lateinit var extensionRegistry: ExtensionRegistry

    override fun read(
        type: Argument<Message>,
        mediaType: MediaType,
        httpHeaders: Headers,
        inputStream: InputStream
    ): Message {
        return type.type.cast(
            codec
                .getBuilder(type)
                .mergeFrom(inputStream, extensionRegistry)
                .build()
        )
    }

    override fun writeTo(
        type: Argument<Message>,
        mediaType: MediaType,
        obj: Message,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, ProtoBufferCodec.PROTO_BUFFER)
        codec.serializeProto(obj, outputStream)
    }
}