package aop.kotlin.plugin.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DebugTimeTrace(val value: String)