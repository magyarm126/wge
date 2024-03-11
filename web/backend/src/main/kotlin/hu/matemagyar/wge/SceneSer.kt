package hu.matemagyar.wge

import hu.matemagyar.wge.model.SceneDto
import io.micronaut.core.type.Argument
import io.micronaut.serde.*
import jakarta.inject.Singleton

@Singleton // (1)
class SceneSer : Serde<SceneDto> { // (2)
    override fun serialize(
        encoder: Encoder?,
        context: Serializer.EncoderContext?,
        type: Argument<out SceneDto>?,
        value: SceneDto?
    ) {
        if (value == null) {
            throw Exception("sError")
        }
        encoder?.encodeInt(value.id)
        encoder?.encodeString(value.name)
    }

    override fun deserialize(
        decoder: Decoder?,
        context: Deserializer.DecoderContext?,
        type: Argument<in SceneDto>?
    ): SceneDto {
        return SceneDto.newBuilder().build()
    }

}