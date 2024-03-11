package hu.matemagyar.wge.proto.convert


import com.google.protobuf.Message
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
import io.micronaut.context.annotation.Requires
import io.micronaut.core.convert.ConversionContext
import io.micronaut.core.convert.TypeConverter
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import jakarta.inject.Singleton
import java.io.IOException
import java.util.*

@Singleton
@Requires(classes = [Message::class, ByteBuf::class])
class ByteBufToProtoMessageConverter(private val codec: ProtoBufferCodec) :
    TypeConverter<ByteBuf, Message?> {
    override fun convert(
        `object`: ByteBuf,
        targetType: Class<Message?>,
        context: ConversionContext
    ): Optional<Message?> {
        return codec
            .getMessageBuilder(targetType)
            .flatMap { builder: Message.Builder ->
                rehydrate(
                    `object`,
                    builder,
                    context
                )
            }
    }

    private fun rehydrate(`object`: ByteBuf, builder: Message.Builder, context: ConversionContext): Optional<Message> {
        try {
            ByteBufInputStream(`object`.copy(), true).use { byteBufInputStream ->
                builder.mergeFrom(byteBufInputStream, codec.extensionRegistry)
                return Optional.of(builder.build())
            }
        } catch (e: IOException) {
            context.reject(e)
            return Optional.empty()
        }
    }
}