package hu.matemagyar.wge.controller

import hu.matemagyar.wge.model.SceneDto
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class SceneController {

    @Get("/scene")
    fun getScene(): SceneDto {
        println("Scene called")
        return SceneDto.newBuilder().setId(1).setName("testScene").build()
    }
}