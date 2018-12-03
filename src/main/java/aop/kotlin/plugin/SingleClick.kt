package aop.kotlin.plugin

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SingleClick(val timeInterval:Long = 600)
