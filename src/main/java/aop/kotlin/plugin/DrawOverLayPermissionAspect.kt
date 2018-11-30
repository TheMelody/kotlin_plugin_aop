package aop.kotlin.plugin

import android.content.Context
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
class DrawOverLayPermissionAspect {

    @Pointcut("execution(@aop.kotlin.plugin.CheckDrawOverLaysPermission * *(..))")
    fun checkDrawOverLaysPermissionMethod() {
    }

    @Around("checkDrawOverLaysPermissionMethod()")
    @Throws(Throwable::class)
    fun checkPermission(joinPoint: ProceedingJoinPoint) {
        val context:Context? = PermissionUtils.getContext(joinPoint.target)
        context?.let {
            if(PermissionUtils.canDrawOverlays(it)){
                try {
                    joinPoint.proceed(joinPoint.args)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }else{
                PermissionUtils.openDrawOverlaysActivity(it)
            }
        }
    }
}