package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.SceneDto
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.protobuf.codec.ProtobufferCodec

@Controller
class SceneController {

    @Get("/scene", processes = [ProtobufferCodec.PROTOBUFFER_ENCODED])
    fun getScene(): SceneDto {
        println("Scene called")
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }
}