package hu.matemagyar.wge.proto.convert

import com.google.protobuf.Message
import io.micronaut.context.annotation.Requires
import io.micronaut.core.convert.ConversionContext
import io.micronaut.core.convert.ConversionService
import io.micronaut.core.convert.TypeConverter
import io.netty.buffer.ByteBuf
import jakarta.inject.Singleton
import java.util.*


@Singleton
@Requires(classes = [Message::class, ByteBuf::class])
class ProtoMessageToByteBufConverter(private val conversionService: ConversionService) :
    TypeConverter<Message, ByteBuf> {
    override fun convert(`object`: Message, targetType: Class<ByteBuf>, context: ConversionContext): Optional<ByteBuf> {
        return conversionService.convert(`object`.toByteArray(), targetType, context)
    }
}