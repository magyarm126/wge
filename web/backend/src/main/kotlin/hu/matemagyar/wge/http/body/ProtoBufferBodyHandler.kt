package hu.matemagyar.wge.http.body

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
class ProtoBufferBodyHandler<T> : MessageBodyHandler<T> {

    @Inject
    lateinit var codec: ProtoBufferCodec

    override fun read(
        type: Argument<T>,
        mediaType: MediaType,
        httpHeaders: Headers,
        inputStream: InputStream
    ): T {
        return codec.decode(type, inputStream)
    }

    override fun writeTo(
        type: Argument<T>,
        mediaType: MediaType,
        obj: T,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, ProtoBufferCodec.PROTO_BUFFER)
        codec.encode(obj, outputStream)
    }
}