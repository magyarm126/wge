package hu.matemagyar.wge.proto.handler

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
import io.micronaut.core.annotation.Order
import io.micronaut.core.type.Argument
import io.micronaut.core.type.Headers
import io.micronaut.core.type.MutableHeaders
import io.micronaut.http.HttpHeaders
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Produces
import io.micronaut.http.body.MessageBodyHandler
import io.micronaut.http.codec.CodecException
import io.micronaut.http.netty.body.NettyJsonHandler
import jakarta.inject.Singleton
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * ProtobufBodyHandler supports endpoint seamless serialization and deserialization based on accept header media type. Tested with json/protobuf on a @Get endpoint.
 *
 * Order must be smaller than 0 for this to be reliable!
 *
 * Inside findWriterImpl in DefaultMessageBodyHandlerRegistry the bean injection finds the first BodyHandler that can write to ANY of the producible types.
 * This means that if we want to have an endpoint that can produce Json and Protobuf, sometimes the 0 order NettyJsonHandler will be collected first.
 * This flakiness happens roughly 50%of the time at the application startup, and can happen while debugging and while running also.
 *
 * List<MessageBodyWriter> ordered = beanDefinitions.stream()
 *                     .sorted(OrderUtil.COMPARATOR)
 *                     .map(beanLocator::getBean)
 *                     .toList();
 * // look for one that's isWriteable first
 * for (MessageBodyWriter writer : ordered) {  // [Netty, this class] or [this class, Netty]
 *      if (mediaTypes.stream().anyMatch(mediaType -> writer.isWriteable(type, mediaType))) { // mediaTypes e.g.: [application/json, application/protobuf]
 *          return writer; // since Netty can write application/json it will short-circuit there, if it is the first element in the foreach
 *      }
 * }
 */
@Order(-1)
@Singleton
@Produces(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2)
@Consumes(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2)
class ProtobufBodyHandler<T>(
    codec: ProtoBufferCodec,
    private val extensionRegistry: ExtensionRegistry,
    private val nettyJsonHandler: NettyJsonHandler<T>
) :
    MessageBodyHandler<T> {
    private val codec: ProtoBufferCodec = codec

    @Throws(CodecException::class)
    override fun read(type: Argument<T>, mediaType: MediaType, httpHeaders: Headers, inputStream: InputStream): T {
        val builder = getBuilder(type)
            .orElseThrow {
                CodecException(
                    "Unable to create builder"
                )
            }
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
        type: Argument<T>,
        mediaType: MediaType,
        obj: T,
        outgoingHeaders: MutableHeaders,
        outputStream: OutputStream
    ) {
        if (obj !is Message) {
            nettyJsonHandler.writeTo(type, mediaType, obj, outgoingHeaders, outputStream)
            return
        }
        if (MediaType.APPLICATION_JSON == mediaType.name) {
            if (obj is Message) {
                outputStream.write(JsonFormat.printer().print(obj).toByteArray())
            } else if (obj is List<*>) {
                obj.forEach {
                    outputStream.write(JsonFormat.printer().print(it as Message).toByteArray())
                }
            }
            return
        }

        outgoingHeaders[HttpHeaders.CONTENT_TYPE] = mediaType ?: ProtoBufferCodec.PROTOBUFFER_ENCODED_TYPE
        try {
            if (obj is Message) {
                obj.writeTo(outputStream)
            } else if (obj is List<*>) {
                obj.forEach {
                    (it as Message).writeDelimitedTo(outputStream)
                }
            }
        } catch (e: IOException) {
            throw CodecException("Failed to write protobuf", e)
        }
    }

    private fun getBuilder(type: Argument<T>): Optional<Message.Builder> {
        return codec.getMessageBuilder(type.type as Class<out Message?>)
    }
}