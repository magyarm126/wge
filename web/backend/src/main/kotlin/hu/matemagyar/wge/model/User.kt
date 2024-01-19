package hu.matemagyar.wge.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class User(name: String) {
    var id: Long = getNextId()
        private set
    var name: String = name
        private set

    init {
        println("User created with name:" + this.name + ", and id:$id")
    }

    companion object Factory{
        private var counter: Long = 0L

        fun getNextId(): Long {
            return counter++
        }

        fun sampleUser(): User {
            return User("NewSampleUser")
        }
    }
}
