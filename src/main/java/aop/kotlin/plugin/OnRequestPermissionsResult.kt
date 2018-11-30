package aop.kotlin.plugin

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnRequestPermissionsResult(val useDefaultDeniedDialog:Boolean = false)