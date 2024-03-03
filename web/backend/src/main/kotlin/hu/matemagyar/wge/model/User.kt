package hu.matemagyar.wge.model

import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Serdeable
@Table(name = "tbl_user")
class User() {

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
