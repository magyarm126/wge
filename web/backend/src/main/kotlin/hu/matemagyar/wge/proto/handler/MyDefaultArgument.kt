package hu.matemagyar.wge.proto.handler

import io.micronaut.core.annotation.AnnotationMetadata
import io.micronaut.core.annotation.Internal
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.type.Argument
import java.util.*

@Internal
internal open class MyDefaultArgument<V, K>
    (private val argument: Argument<V>, private val value: K) : Argument<V> {
    override fun getName(): String {
        return argument.name
    }

    override fun getType(): Class<V> {
        return argument.type
    }

    override fun getFirstTypeVariable(): Optional<Argument<*>> {
        return argument.firstTypeVariable
    }

    override fun getTypeParameters(): Array<Argument<*>> {
        return argument.typeParameters
    }

    override fun getTypeVariables(): Map<String, Argument<*>> {
        return argument.typeVariables
    }

    fun getValue(): K {
        return value
    }

    override fun <T : Annotation?> synthesize(annotationClass: Class<T>): T {
        return argument.synthesize(annotationClass)
    }

    override fun <T : Annotation?> synthesize(
        annotationClass: @NonNull Class<T>?,
        sourceAnnotation: @NonNull String?
    ): @Nullable T? {
        return argument.synthesize(annotationClass, sourceAnnotation)
    }

    override fun <T : Annotation?> synthesizeDeclared(
        annotationClass: @NonNull Class<T>?,
        sourceAnnotation: @NonNull String?
    ): @Nullable T? {
        return argument.synthesizeDeclared(annotationClass, sourceAnnotation)
    }

    override fun synthesizeAll(): Array<Annotation> {
        return argument.synthesizeAll()
    }

    override fun synthesizeDeclared(): Array<Annotation> {
        return argument.synthesizeDeclared()
    }

    override fun equalsType(o: @Nullable Argument<*>?): Boolean {
        return argument.equalsType(o)
    }

    override fun typeHashCode(): Int {
        return argument.typeHashCode()
    }

    override fun getAnnotationMetadata(): AnnotationMetadata {
        return argument.annotationMetadata
    }
}