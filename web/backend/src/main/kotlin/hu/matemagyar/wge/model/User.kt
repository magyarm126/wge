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
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = getNextId()
        private set

    @Column(name = "name", nullable = true, unique = false)
    var name: String = ""
        private set

    init {
        println("User created with name:" + this.name + ", and id:$id")
    }

    constructor(name: String) : this() {
        this.name = name
    }

    companion object Factory {
        private var counter: Long = 0L

        fun getNextId(): Long {
            return counter++
        }

        fun sampleUser(): User {
            return User("NewSampleUser")
        }
    }
}
