package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.SceneDto
import hu.matemagyar.wge.proto.codec.ProtoBufferCodec
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class SceneController {

    @Get("/scene", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getScene(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneJson")
    fun getSceneJson(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneProto", produces = [ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneProto(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneProtos", produces = [ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneProtos(): List<SceneDto> {
        return listOf(SceneDto.newBuilder().setId(1).setName("testScene").build(), SceneDto.newBuilder().setId(2).setName("testScene2").build()) }
}