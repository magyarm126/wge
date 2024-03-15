package hu.matemagyar.wge.proto.serde


import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import io.micronaut.core.type.Argument
import io.micronaut.json.tree.JsonNode
import io.micronaut.serde.*
import jakarta.inject.Singleton
import org.json.JSONObject

@Singleton
class ProtoMessageSerdeBean : Serde<Message> {


    override fun serialize(
        encoder: Encoder?,
        context: Serializer.EncoderContext?,
        type: Argument<out Message>?,
        value: Message?
    ) {
        if (value == null) {
            throw Exception("Could not serialize MessageOrBuilder, value is null for type:" + type?.simpleName)
        }

        val jsonRootObj = JSONObject(JsonFormat.printer().print(value))

        if (encoder != null && context != null) {
            encode(encoder, context, jsonRootObj)
        }
    }

    private fun encode(
        encoder: Encoder,
        context: Serializer.EncoderContext,
        jsonObject: JSONObject
    ) {
        encoder.encodeObject(Argument.of(JsonNode::class.java))

        for (key in jsonObject.keys()) {
            val jso = jsonObject.get(key)
            encoder.encodeKey(key)
            if (jso is String) {
                encoder.encodeString(jso)
                continue
            }
            if (jso is Int) {
                encoder.encodeInt(jso)
                continue
            }
            encode(encoder, context, jso as JSONObject)
        }
        encoder.finishStructure()
    }

    override fun deserialize(
        decoder: Decoder?,
        context: Deserializer.DecoderContext?,
        type: Argument<in Message>?
    ): Message {
        throw RuntimeException("No deserializer for MessageLite")
    }

}