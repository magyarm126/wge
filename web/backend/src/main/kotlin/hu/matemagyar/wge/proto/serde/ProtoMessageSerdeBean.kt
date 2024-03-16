package hu.matemagyar.wge.proto.serde


import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
import hu.matemagyar.wge.proto.handler.MyDefaultArgument
import io.micronaut.core.type.Argument
import io.micronaut.json.JsonMapper
import io.micronaut.json.tree.JsonNode
import io.micronaut.serde.*
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ProtoMessageSerdeBean<T : Message> : Serde<T> {

    @Inject
    lateinit var objectMapper: JsonMapper

    @Inject
    lateinit var codec: ProtoBufferCodec

    override fun serialize(encoder: Encoder?, context: Serializer.EncoderContext?, type: Argument<out T>?, value: T) {
        context!!.findSerializer(JsonNode::class.java).serialize(
            encoder,
            context,
            Argument.of(JsonNode::class.java),
            objectMapper.readValue(JsonFormat.printer().print(value), JsonNode::class.java)
        )
    }

    override fun deserialize(decoder: Decoder?, context: Deserializer.DecoderContext?, type: Argument<in T>?): T {
        val rootJson: JsonNode = decoder!!.decodeNode()
        val writeValueAsString = objectMapper.writeValueAsString(rootJson)

        val jsonBuilder = if (type is MyDefaultArgument<*, *>) {
            codec.getBuilder(type.getValue() as Argument<*>)
        } else {
            codec.getBuilder(type!!)
        }
        JsonFormat.parser().merge(writeValueAsString, jsonBuilder)
        return type.type.cast(jsonBuilder.build()) as T
    }
}