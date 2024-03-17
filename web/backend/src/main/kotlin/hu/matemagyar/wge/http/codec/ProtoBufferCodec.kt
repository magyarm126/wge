package hu.matemagyar.wge.http.codec

import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Message
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.buffer.ByteBuffer
import io.micronaut.core.io.buffer.ByteBufferFactory
import io.micronaut.core.type.Argument
import io.micronaut.http.MediaType
import io.micronaut.http.codec.CodecException
import io.micronaut.http.codec.MediaTypeCodec
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Named("ProtoBufferCodec")
class ProtoBufferCodec
    (
    val extensionRegistry: ExtensionRegistry
) : MediaTypeCodec {
    private val methodCache = ConcurrentHashMap<Class<*>, Method?>()

    private var mediaTypes = DEFAULT_MEDIA_TYPES

    override fun supportsType(type: Class<*>?): Boolean {
        return Message::class.java.isAssignableFrom(type)
    }

    override fun getMediaTypes(): Collection<MediaType> {
        return mediaTypes
    }

    fun setMediaTypes(mediaTypes: List<MediaType>?) {
        this.mediaTypes = Collections.unmodifiableList(ArrayList(mediaTypes))
    }

    @Throws(CodecException::class)
    override fun <T> decode(type: Argument<T>, inputStream: InputStream): T {
        try {
            val builder = getBuilder<T>(type)
                .orElseThrow {
                    CodecException(
                        "Unable to create builder"
                    )
                }
            check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
            builder.mergeFrom(inputStream, extensionRegistry)
            return type.type.cast(builder.build())
        } catch (e: Exception) {
            throw CodecException("Error decoding Protobuff stream for type [" + type.name + "]: " + e.message, e)
        }
    }

    @Throws(CodecException::class)
    override fun <T> decode(type: Argument<T>, buffer: ByteBuffer<*>): T {
        try {
            if (type.type == ByteArray::class.java) {
                return buffer.toByteArray() as T
            } else {
                val builder = getBuilder<T>(type)
                    .orElseThrow {
                        CodecException(
                            "Unable to create builder"
                        )
                    }
                check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
                builder.mergeFrom(buffer.toByteArray(), extensionRegistry)
                return type.type.cast(builder.build())
            }
        } catch (e: Exception) {
            throw CodecException("Error decoding Protobuff bytes for type [" + type.name + "]: " + e.message, e)
        }
    }

    @Throws(CodecException::class)
    override fun <T> decode(type: Argument<T>, bytes: ByteArray): T {
        try {
            if (type.type == ByteArray::class.java) {
                return bytes as T
            } else {
                val builder = getBuilder<T>(type)
                    .orElseThrow {
                        CodecException(
                            "Unable to create builder"
                        )
                    }
                check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
                builder.mergeFrom(bytes, extensionRegistry)
                return type.type.cast(builder.build())
            }
        } catch (e: Exception) {
            throw CodecException("Error decoding Protobuff bytes for type [" + type.name + "]: " + e.message, e)
        }
    }

    private fun <T> getBuilder(type: Argument<T>): Optional<Message.Builder> {
        val clazz = type.type as Class<out Message?>
        return getMessageBuilder(clazz)
    }

    @Throws(CodecException::class)
    override fun <T> encode(`object`: T, outputStream: OutputStream) {
        try {
            if (`object` is Message) {
                `object`.writeTo(outputStream)
            }
        } catch (e: IOException) {
            throw CodecException("Error encoding object [" + `object` + "] to OutputStream:" + e.message)
        }
    }

    @Throws(CodecException::class)
    override fun <T> encode(`object`: T): ByteArray {
        if (`object` is Message) {
            return `object`.toByteArray()
        } else if (`object` is ByteArray) {
            return `object`
        }
        return ByteArray(0)
    }

    @Throws(CodecException::class)
    override fun <T, B> encode(`object`: T, allocator: ByteBufferFactory<*, B>): ByteBuffer<B> {
        return allocator.copiedBuffer(encode(`object`))
    }

    fun getMessageBuilder(clazz: Class<out Message?>): Optional<Message.Builder> {
        return try {
            createBuilder(clazz)
        } catch (throwable: Throwable) {
            Optional.empty()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getBuilder(type: Argument<*>): Message.Builder {
        return getMessageBuilder(type.type as Class<out Message?>).orElseThrow()
    }

    @Throws(Exception::class)
    private fun createBuilder(clazz: Class<out Message?>): Optional<Message.Builder> {
        return Optional.of(getMethod(clazz)!!.invoke(clazz) as Message.Builder)
    }

    @Throws(NoSuchMethodException::class)
    private fun getMethod(clazz: Class<out Message?>): Method? {
        var method = methodCache[clazz]
        if (method == null) {
            method = clazz.getMethod("newBuilder")
            methodCache[clazz] = method
        }
        return method
    }

    companion object {
        const val X_PROTOBUF_MESSAGE_HEADER: String = "X-Protobuf-Message"
        const val PROTOBUFFER_ENCODED: String = "application/x-protobuf"
        const val PROTOBUFFER_ENCODED2: String = "application/protobuf"
        val PROTOBUFFER_ENCODED_TYPE: MediaType = MediaType(PROTOBUFFER_ENCODED)
        val PROTOBUFFER_ENCODED_TYPE2: MediaType = MediaType(PROTOBUFFER_ENCODED2)
        val DEFAULT_MEDIA_TYPES: List<MediaType> =
            java.util.List.of(PROTOBUFFER_ENCODED_TYPE, PROTOBUFFER_ENCODED_TYPE2)
    }

    fun serializeProto(message: Message, outputStream: OutputStream, delimited: Boolean) {
        if (delimited) message.writeDelimitedTo(outputStream)
        else message.writeTo(outputStream)
    }

    fun serializeProtos(messageList: List<Message>, outputStream: OutputStream) {
        messageList.forEach {
            serializeProto(it, outputStream, messageList.size > 1)
        }
    }
}

@Factory
@Requires(classes = [ExtensionRegistry::class])
open class ExtensionRegistryFactory {
    @Singleton
    protected fun extensionRegistry(): ExtensionRegistry {
        return ExtensionRegistry.newInstance()
    }
}