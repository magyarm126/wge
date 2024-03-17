package hu.matemagyar.wge.bodyhandler

import hu.matemagyar.wge.proto.ProtoBufferBodyHandler
import hu.matemagyar.wge.proto.ProtoBufferCodec
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
@Produces(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2, MediaType.APPLICATION_JSON)
@Consumes(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2, MediaType.APPLICATION_JSON)
class ProtoBufferBodyHandlerProxy<T> : MessageBodyHandler<T> {

    @Inject
    lateinit var jsonBodyHandler: NettyJsonHandler<T>

    @Inject
    lateinit var protoBufferBodyHandler: ProtoBufferBodyHandler<T>

    @Throws(CodecException::class)
    override fun read(type: Argument<T>, mediaType: MediaType, httpHeaders: Headers, inputStream: InputStream): T {
        if (isJson(mediaType)) {
            return jsonBodyHandler.read(type, mediaType, httpHeaders, inputStream)
        }
        return protoBufferBodyHandler.read(type, mediaType, httpHeaders, inputStream)
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
        return protoBufferBodyHandler.writeTo(type, mediaType, obj, outgoingHeaders, outputStream)
    }

    private fun isJson(mediaType: MediaType) = MediaType.APPLICATION_JSON == mediaType.name
}