package hu.matemagyar.wge.proto.serde


import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.google.protobuf.Message
import jakarta.inject.Singleton

@Singleton
class ProtoDeser : JsonDeserializer<Message>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Message {
        TODO("Not yet implemented")
    }

}