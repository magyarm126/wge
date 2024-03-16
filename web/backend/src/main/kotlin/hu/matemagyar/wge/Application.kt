package hu.matemagyar.wge

import com.google.protobuf.Message
import io.micronaut.runtime.Micronaut
import io.micronaut.serde.annotation.SerdeImport


/**
 * If we need to add the mainClass to Micronaut, wrap it inside a companion object and use the @JvmStatic annotation.
 *
 * object Application {
 *
 *     @JvmStatic
 *     fun main(args: Array<String>) {
 *         Micronaut.build().packages("hu.matemagyar.wge")
 *             .mainClass(Application.javaClass)
 *             .start()
 *     }
 * }
 *
 * In the gradle.properties, don't forget to change the mainClass to the companion objects name
 *
 * application {
 *     mainClass.set("hu.matemagyar.wge.Application")
 * }
 *
 */
fun main() {
    Micronaut.build()
        .packages("hu.matemagyar.wge")
        .start()
}

@SerdeImport(packageName = "hu.matemagyar.wge.model")
class Serdes {}