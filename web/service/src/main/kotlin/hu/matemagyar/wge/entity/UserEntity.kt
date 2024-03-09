package hu.matemagyar.wge.entity

import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.*

@Entity
@Serdeable
@Table(name = "tbl_user")
class UserEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name", nullable = true, unique = false)
    var name: String = ""

    init {
        println("User created with name:" + this.name + ", and id:$id")
    }

    constructor(name: String) : this() {
        this.name = name
    }
}
