package hu.matemagyar.wge.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class UserDto {

    var id: Long? = null
    var name: String = ""
}
