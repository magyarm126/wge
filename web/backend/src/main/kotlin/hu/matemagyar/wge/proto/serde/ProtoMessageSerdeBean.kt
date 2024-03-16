package hu.matemagyar.wge.proto.serde


import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
import io.micronaut.core.type.Argument
import io.micronaut.json.JsonMapper
import io.micronaut.json.tree.JsonNode
import io.micronaut.serde.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class ProtoMessageSerdeBean : Serde<Message> {

    @Inject
    lateinit var objectMapper: JsonMapper

    @Inject
    lateinit var defaultSerdeRegistry: SerdeRegistry

    @Inject
    lateinit var codec: ProtoBufferCodec


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
        if (decoder == null || type == null) {
            throw RuntimeException("meh")
        }

        val rootJson: JsonNode = decoder.decodeNode()
        val writeValueAsString = objectMapper.writeValueAsString(rootJson)
        val jsonBuilder = getBuilder(type).orElseThrow()
        JsonFormat.parser().merge(writeValueAsString, jsonBuilder)
        return type.type.cast(jsonBuilder.build()) as Message
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBuilder(type: Argument<*>): Optional<Message.Builder> {
        return codec.getMessageBuilder(type.type as Class<out Message?>)
    }

}