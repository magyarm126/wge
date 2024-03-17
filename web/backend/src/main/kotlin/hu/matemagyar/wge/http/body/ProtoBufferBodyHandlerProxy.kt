package hu.matemagyar.wge.http.body

import com.google.protobuf.Message
import hu.matemagyar.wge.http.codec.ProtoBufferCodec
import io.micronaut.core.type.Argument
import io.micronaut.core.type.Headers
import io.micronaut.core.type.MutableHeaders
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Produces
import io.micronaut.http.body.MessageBodyHandler
import io.micronaut.http.codec.CodecException
import io.micronaut.http.netty.body.NettyJsonHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@Singleton
@Suppress("MnInjectionPoints")
@Produces(ProtoBufferCodec.PROTO_BUFFER, ProtoBufferCodec.PROTO_BUFFER, MediaType.APPLICATION_JSON)
@Consumes(ProtoBufferCodec.PROTO_BUFFER, ProtoBufferCodec.PROTO_BUFFER, MediaType.APPLICATION_JSON)
class ProtoBufferBodyHandlerProxy<T> : MessageBodyHandler<T> {

    @Inject
    lateinit var jsonBodyHandler: NettyJsonHandler<T>

    @Inject
    lateinit var protoBufferBodyHandler: ProtoBufferBodyHandler

    @Inject
    lateinit var protoBufferBodyListHandler: ProtoBufferBodyListHandler

    @Throws(CodecException::class)
    override fun read(type: Argument<T>, mediaType: MediaType, httpHeaders: Headers, inputStream: InputStream): T {
        if (isJson(mediaType)) {
            return jsonBodyHandler.read(type, mediaType, httpHeaders, inputStream)
        }
        if (type.name == "java.lang.List") {
            return protoBufferBodyListHandler.read(
                type as Argument<List<Message>>,
                mediaType,
                httpHeaders,
                inputStream
            ) as T
        }
        return protoBufferBodyHandler.read(type as Argument<Message>, mediaType, httpHeaders, inputStream) as T
    }

    @Throws(CodecException::class)
    override fun writeTo(
        type: Argument<T>,
        mediaType: MediaType,
        obj: T,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        if (isJson(mediaType)) {
            return jsonBodyHandler.writeTo(type, mediaType, obj, outgoingHeaders, outputStream)
        }
        if (Collection::class.java.isAssignableFrom(type.type)) {
            return protoBufferBodyListHandler.writeTo(
                type as Argument<List<Message>>,
                mediaType,
                obj as List<Message>,
                outgoingHeaders,
                outputStream
            )

        }
        return protoBufferBodyHandler.writeTo(
            type as Argument<Message>,
            mediaType,
            obj as Message,
            outgoingHeaders,
            outputStream
        )
    }

    private fun isJson(mediaType: MediaType) = MediaType.APPLICATION_JSON == mediaType.name
}