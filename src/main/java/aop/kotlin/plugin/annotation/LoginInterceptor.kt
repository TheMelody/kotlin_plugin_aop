package aop.kotlin.plugin.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LoginInterceptor(val interceptorType:Int = 0)
