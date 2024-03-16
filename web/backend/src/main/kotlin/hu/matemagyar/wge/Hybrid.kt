package hu.matemagyar.wge

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.google.protobuf.Message
import hu.matemagyar.wge.model.SceneDto
import hu.matemagyar.wge.proto.serde.ProtoDeser
import hu.matemagyar.wge.proto.serde.ProtoMessageSerdeBean
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Hybrid {

    var id: Long? = 13
    var name: String = "sdaad"

    @JsonDeserialize(`as` = Message::class)
    var sceneDtoMember: SceneDto = SceneDto.newBuilder().setName("asyd").setId(3).build()
}
