package aop.kotlin.plugin.aspectj

import android.content.Context
import aop.kotlin.plugin.utils.PermissionUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
internal class DrawOverLayPermissionAspect {

    @Pointcut("execution(@aop.kotlin.plugin.annotation.CheckDrawOverLaysPermission * *(..))")
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