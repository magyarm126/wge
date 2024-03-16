package hu.matemagyar.wge.controller

import hu.matemagyar.wge.Hybrid
import hu.matemagyar.wge.model.SceneDto
import hu.matemagyar.wge.proto.ProtoBufferCodec
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

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

    @Get("/sceneHybrid", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneHybrid(): Hybrid {
        return Hybrid()
    }

    @Post("/scenePostHybrid", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun postSceneHybrid(@Body dto: Hybrid): Hybrid {
        println(dto)
        return dto
    }

    @Post("/scenePostHybrids", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun postSceneHybrids(@Body dto: List<Hybrid>): List<Hybrid> {
        println(dto)
        return dto
    }

    @Get("/sceneProto", produces = [ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneProto(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneNull", produces = [ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneNull(): SceneDto? {
        return null
    }

    @Get("/sceneResponse", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneResponse(): HttpResponse<SceneDto> {
        return HttpResponse.created(
            SceneDto.newBuilder().setId(1).setName("testScene").build()
        )
    }

    @Get("/sceneProtos", produces = ["application/json", ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun getSceneProtos(): List<SceneDto> {
        return listOf(
            SceneDto.newBuilder().setId(1).setName("testScene").build(),
            SceneDto.newBuilder().setId(2).setName("testScene2").build()
        )
    }

    @Post("/scenePost", processes = [MediaType.APPLICATION_JSON, ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun postProto(@Body dto: SceneDto): SceneDto {
        println(dto)
        return dto
    }

    @Post("/scenePosts", processes = [MediaType.APPLICATION_JSON, ProtoBufferCodec.PROTOBUFFER_ENCODED2])
    fun postProtos(@Body dto: List<SceneDto>): List<SceneDto> {
        println(dto)
        return dto
    }
}