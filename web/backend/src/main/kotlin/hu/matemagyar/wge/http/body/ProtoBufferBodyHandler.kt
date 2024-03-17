package hu.matemagyar.wge.http.body

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
import hu.matemagyar.wge.http.codec.ProtoBufferCodec
import io.micronaut.core.type.Argument
import io.micronaut.core.type.Headers
import io.micronaut.core.type.MutableHeaders
import io.micronaut.http.HttpHeaders
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Produces
import io.micronaut.http.body.MessageBodyHandler
import io.micronaut.http.codec.CodecException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@Singleton
@Produces(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2)
@Consumes(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2)
class ProtoBufferBodyHandler : MessageBodyHandler<Message> {

    @Inject
    lateinit var codec: ProtoBufferCodec

    @Inject
    lateinit var extensionRegistry: ExtensionRegistry

    @Throws(CodecException::class)
    override fun read(
        type: Argument<Message>,
        mediaType: MediaType,
        httpHeaders: Headers,
        inputStream: InputStream
    ): Message {
        val builder = codec.getBuilder(type)
        check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
        try {
            builder.mergeFrom(inputStream, extensionRegistry)
        } catch (e: IOException) {
            throw CodecException("Failed to read protobuf", e)
        }
        return type.type.cast(builder.build())
    }

    @Throws(CodecException::class)
    override fun writeTo(
        type: Argument<Message>,
        mediaType: MediaType,
        obj: Message,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, ProtoBufferCodec.PROTOBUFFER_ENCODED)
        try {
            codec.serializeProto(obj, outputStream, false)
        } catch (exception: IOException) {
            throw CodecException("Proto serialization error", exception)
        }
    }
}