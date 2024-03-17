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
class ProtoBufferBodyListHandler : MessageBodyHandler<List<Message>> {

    @Inject
    lateinit var codec: ProtoBufferCodec

    @Inject
    lateinit var extensionRegistry: ExtensionRegistry

    override fun read(
        type: Argument<List<Message>>,
        mediaType: MediaType,
        httpHeaders: Headers,
        inputStream: InputStream
    ): List<Message> {
        throw RuntimeException("We can't deserialize proto lists yet" + type.type.name)
    }

    override fun writeTo(
        type: Argument<List<Message>>,
        mediaType: MediaType,
        obj: List<Message>,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, ProtoBufferCodec.PROTO_BUFFER)
        codec.serializeProtos(obj, outputStream)
    }
}