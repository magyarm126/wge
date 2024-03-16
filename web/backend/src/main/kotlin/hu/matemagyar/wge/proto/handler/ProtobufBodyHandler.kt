package hu.matemagyar.wge.proto.handler

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
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
//@Order(-1)
//@Singleton
@Produces(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2, MediaType.APPLICATION_JSON)
@Consumes(ProtoBufferCodec.PROTOBUFFER_ENCODED, ProtoBufferCodec.PROTOBUFFER_ENCODED2, MediaType.APPLICATION_JSON)
class ProtobufBodyHandler<T>(
    codec: ProtoBufferCodec,
    private val extensionRegistry: ExtensionRegistry,
    private val nettyJsonHandler: NettyJsonHandler<T>
) :
    MessageBodyHandler<T> {
    private val codec: ProtoBufferCodec = codec

    @Throws(CodecException::class)
    override fun read(type: Argument<T>, mediaType: MediaType, httpHeaders: Headers, inputStream: InputStream): T {
        val isJson = MediaType.APPLICATION_JSON == mediaType.name
        /*        if (isJson) {

                    if (type is Message) {


                        try {
                            val jsonBuilder = getBuilderTyped(type).orElseThrow()
                            JsonFormat.parser().merge(InputStreamReader(inputStream), jsonBuilder)
                            return type.type.cast(jsonBuilder.build())
                        } catch (e: IOException) {
                            throw CodecException("Failed to read protobuf from JSON", e)
                        }
                    }

                    val returnList = ArrayList<Message>()
                    val jsonArrayString = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(jsonArrayString)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val jsonBuilder = getBuilder(type.typeVariables.values.first()).orElseThrow()
                        JsonFormat.parser().merge(jsonObject.toString(), jsonBuilder)
                        val msg: Message = jsonBuilder.build() as Message
                        returnList.addLast(msg)
                    }
                    return type.type.cast(returnList)
                }*/

        val builder = getBuilderTyped(type)
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
        if (MediaType.APPLICATION_JSON == mediaType.name) {
            nettyJsonHandler.writeTo(type, mediaType, obj, outgoingHeaders, outputStream)
            return
        }
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
    private fun getBuilderTyped(type: Argument<T>): Optional<Message.Builder> {
        return codec.getMessageBuilder(type.type as Class<out Message?>)
    }
}