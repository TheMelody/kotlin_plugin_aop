package aop.kotlin.plugin.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnRequestPermissionsResult(val useDefaultDeniedDialog:Boolean = false)