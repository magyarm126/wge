package hu.matemagyar.wge

import hu.matemagyar.wge.model.SceneDto
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Hybrid {

    var id: Long? = 13
    var name: String = "sdaad"
    var sceneDtoMember: SceneDto = SceneDto.newBuilder().setName("asyd").setId(3).build()
}
