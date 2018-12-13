package aop.kotlin.plugin.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CheckPermission(val requestCode:Int = 100,val permissions:Array<String>)