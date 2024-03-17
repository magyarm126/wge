package hu.matemagyar.wge.controller

import hu.matemagyar.wge.http.codec.ProtoBufferCodec
import hu.matemagyar.wge.model.Hybrid
import hu.matemagyar.wge.model.SceneDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller
class SceneController {

    @Get("/scene", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun getScene(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneJson")
    fun getSceneJson(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneHybrid", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun getSceneHybrid(): Hybrid {
        return Hybrid()
    }

    @Post("/scenePostHybrid", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun postSceneHybrid(@Body dto: Hybrid): Hybrid {
        println(dto)
        return dto
    }

    @Post("/scenePostHybrids", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun postSceneHybrids(@Body dto: List<Hybrid>): List<Hybrid> {
        println(dto)
        return dto
    }

    @Get("/sceneProto", processes = [ProtoBufferCodec.PROTO_BUFFER])
    fun getSceneProto(): SceneDto {
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }

    @Get("/sceneNull", processes = [ProtoBufferCodec.PROTO_BUFFER])
    fun getSceneNull(): SceneDto? {
        return null
    }

    @Get("/sceneResponse", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun getSceneResponse(): HttpResponse<SceneDto> {
        return HttpResponse.created(
            SceneDto.newBuilder().setId(1).setName("testScene").build()
        )
    }

    @Get("/sceneProtos", processes = ["application/json", ProtoBufferCodec.PROTO_BUFFER])
    fun getSceneProtos(): List<SceneDto> {
        return listOf(
            SceneDto.newBuilder().setId(1).setName("testScene").build(),
            SceneDto.newBuilder().setId(2).setName("testScene2").build()
        )
    }

    @Post("/scenePost", processes = [MediaType.APPLICATION_JSON, ProtoBufferCodec.PROTO_BUFFER])
    fun postProto(@Body dto: SceneDto): SceneDto {
        println(dto)
        return dto
    }

    @Post("/scenePosts", processes = [MediaType.APPLICATION_JSON, ProtoBufferCodec.PROTO_BUFFER])
    fun postProtos(@Body dto: List<SceneDto>): List<SceneDto> {
        println(dto)
        return dto
    }
}