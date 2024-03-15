package hu.matemagyar.wge.proto.serde


import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import io.micronaut.core.type.Argument
import io.micronaut.json.JsonMapper
import io.micronaut.json.tree.JsonNode
import io.micronaut.serde.*
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ProtoMessageSerdeBean : Serde<Message> {

    @Inject
    lateinit var objectMapper: JsonMapper

    @Inject
    lateinit var defaultSerdeRegistry: SerdeRegistry

    override fun serialize(
        encoder: Encoder?,
        context: Serializer.EncoderContext?,
        type: Argument<out Message>?,
        value: Message?
    ) {
        defaultSerdeRegistry.findSerializer(JsonNode::class.java).serialize(
            encoder,
            context,
            Argument.of(JsonNode::class.java),
            objectMapper.readValue(JsonFormat.printer().print(value), JsonNode::class.java)
        )
    }

    override fun deserialize(
        decoder: Decoder?,
        context: Deserializer.DecoderContext?,
        type: Argument<in Message>?
    ): Message {
        throw RuntimeException("No deserializer for MessageLite")
    }

}