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
        var a = decoder?.decodeObject(Argument.of(JsonNode::class.java))
        a.toString()
        /*if (type is Message) {
         *


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
        throw RuntimeException("No deserializer for MessageLite")
        */
        return null as Message
    }

}