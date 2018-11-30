package aop.kotlin.plugin

import android.util.Log
import aop.kotlinx.plugin.BuildConfig
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.aspectj.lang.reflect.SourceLocation

@Aspect
internal class DebugTimeTraceAspect {
    companion object {
        const val TAG = "TimeTrace"
    }

    @Pointcut("execution(@aop.kotlin.plugin.DebugTimeTrace * *(..))")
    fun timeTraceMethod() {
    }

    @Around("timeTraceMethod()")
    @Throws(Throwable::class)
    fun methodTimeTrace(joinPoint: ProceedingJoinPoint) {
        if(BuildConfig.DEBUG_LOG){
            val methodSignature: MethodSignature = joinPoint.signature as MethodSignature
            val methodLocation: SourceLocation = joinPoint.sourceLocation
            val methodLine = methodLocation.line
            val className = methodSignature.declaringType.simpleName
            val methodName = methodSignature.name
            val timeTrace: DebugTimeTrace = methodSignature.method.getAnnotation(DebugTimeTrace::class.java)
            val value = timeTrace.value
            val startTime = System.currentTimeMillis()
            joinPoint.proceed()
            val duration: Long = System.currentTimeMillis() - startTime
            Log.d(TAG, String.format("ClassName:【%s】,Line:【%s】,Method:【%s】,【%s】耗时:【%dms】", className, methodLine, methodName,value, duration))
        }else{
            joinPoint.proceed()
        }
    }
}