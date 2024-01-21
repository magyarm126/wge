package hu.matemagyar.wge

import io.micronaut.runtime.Micronaut


object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build().packages("hu.matemagyar.wge")
            .mainClass(Application.javaClass)
            .start()
    }
}

