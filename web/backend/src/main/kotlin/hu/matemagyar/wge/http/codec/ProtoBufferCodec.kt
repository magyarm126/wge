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
import jakarta.inject.Inject
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
class ProtoBufferCodec : MediaTypeCodec {
    private val methodCache = ConcurrentHashMap<Class<*>, Method>()

    private var mediaTypes = listOf(PROTO_BUFFER_TYPE)

    @Inject
    lateinit var extensionRegistry: ExtensionRegistry

    companion object {
        const val PROTO_BUFFER: String = "application/x-protobuf"
        val PROTO_BUFFER_TYPE: MediaType = MediaType(PROTO_BUFFER)
    }

    override fun supportsType(type: Class<*>): Boolean {
        return Message::class.java.isAssignableFrom(type)
    }

    override fun getMediaTypes(): Collection<MediaType> {
        return mediaTypes
    }

    @Throws(CodecException::class)
    override fun <T> decode(type: Argument<T>, inputStream: InputStream): T {
        try {
            val builder = getBuilder(type)
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
                val builder = getBuilder(type)
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
                val builder = getBuilder(type)
                check(!type.hasTypeVariables()) { "Generic type arguments are not supported" }
                builder.mergeFrom(bytes, extensionRegistry)
                return type.type.cast(builder.build())
            }
        } catch (e: Exception) {
            throw CodecException("Error decoding Protobuf bytes for type [" + type.name + "]: " + e.message, e)
        }
    }

    override fun <T> encode(obj: T, outputStream: OutputStream) {
        try {
            if (obj is Message) {
                obj.writeTo(outputStream)
            }
        } catch (e: IOException) {
            throw CodecException("Error encoding object [" + obj + "] to OutputStream:" + e.message)
        }
    }

    override fun <T> encode(obj: T): ByteArray {
        if (obj is Message) {
            return obj.toByteArray()
        } else if (obj is ByteArray) {
            return obj
        }
        return ByteArray(0)
    }

    override fun <T, B> encode(obj: T, allocator: ByteBufferFactory<*, B>): ByteBuffer<B> {
        return allocator.copiedBuffer(encode(obj))
    }

    fun getBuilder(type: Argument<*>): Message.Builder {
        val clazz = type.type
        try {
            val method = methodCache.getOrDefault(clazz, clazz.getMethod("newBuilder"))
            return method.invoke(clazz) as Message.Builder
        } catch (throwable: Throwable) {
            throw CodecException("Could not create message builder for class:" + clazz.simpleName, throwable)
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