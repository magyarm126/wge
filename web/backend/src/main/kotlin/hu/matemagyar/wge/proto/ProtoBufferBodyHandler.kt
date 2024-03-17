package hu.matemagyar.wge.proto

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
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
class ProtoBufferBodyHandler<T> : MessageBodyHandler<T> {

    @Inject
    lateinit var codec: ProtoBufferCodec

    @Inject
    lateinit var extensionRegistry: ExtensionRegistry

    @Throws(CodecException::class)
    override fun read(type: Argument<T>, mediaType: MediaType, httpHeaders: Headers, inputStream: InputStream): T {
        val isList = type.type.name.equals("java.util.List")
        if (isList) {
            throw RuntimeException("We can't deserialize proto lists yet" + type.type.name)
        }
        if (!Message::class.java.isAssignableFrom(type.type)) {
            throw RuntimeException("We dont proto hybrids" + type.type.name)
        }
        val builder = getBuilder(type)
            .orElseThrow {
                CodecException(
                    "Unable to create builder"
                )
            }
        check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
        try {
            builder.mergeFrom(inputStream, extensionRegistry)
            //builder.mergeDelimitedFrom()
        } catch (e: IOException) {
            throw CodecException("Failed to read protobuf", e)
        }
        return type.type.cast(builder.build())
    }

    @Throws(CodecException::class)
    override fun writeTo(
        type: Argument<T>,
        mediaType: MediaType,
        obj: T,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, ProtoBufferCodec.PROTOBUFFER_ENCODED)
        try {
            if (obj is List<*>) {
                serializeProtos(obj.filterIsInstance<Message>(), outputStream)
            } else {
                serializeProto(obj as Message, outputStream, false)
            }
        } catch (exception: IOException) {
            throw CodecException("Proto serialization error", exception)
        }
    }

    private fun serializeProto(message: Message, outputStream: OutputStream, delimited: Boolean) {
        if (delimited) message.writeDelimitedTo(outputStream)
        else message.writeTo(outputStream)
    }

    private fun serializeProtos(messageList: List<Message>, outputStream: OutputStream) {
        messageList.forEach {
            serializeProto(it, outputStream, messageList.size > 1)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBuilder(type: Argument<*>): Optional<Message.Builder> {
        return codec.getMessageBuilder(type.type as Class<out Message?>)
    }
}