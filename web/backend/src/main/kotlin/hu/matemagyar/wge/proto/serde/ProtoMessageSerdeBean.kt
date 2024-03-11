package hu.matemagyar.wge.proto.serde

import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.util.JsonFormat
import io.micronaut.core.type.Argument
import io.micronaut.serde.*
import jakarta.inject.Singleton
import jdk.jshell.spi.ExecutionControl.NotImplementedException

@Singleton
class ProtoMessageSerdeBean : Serde<MessageOrBuilder> {
    override fun serialize(
        encoder: Encoder?,
        context: Serializer.EncoderContext?,
        type: Argument<out MessageOrBuilder>?,
        value: MessageOrBuilder?
    ) {
        if (value == null) {
            throw Exception("Could not serialize MessageOrBuilder, value is null for type:" + type?.simpleName)
        }
        encoder?.encodeString(JsonFormat.printer().print(value))
    }

    override fun deserialize(
        decoder: Decoder?,
        context: Deserializer.DecoderContext?,
        type: Argument<in MessageOrBuilder>?
    ): MessageOrBuilder {
        throw NotImplementedException("No deserializer for MessageLite")
    }

}