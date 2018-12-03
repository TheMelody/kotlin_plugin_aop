package aop.kotlin.plugin

import android.util.Log
import aop.kotlinx.plugin.BuildConfig
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.*

@Aspect
internal class SingleClickAspect {

    @Pointcut("execution(@aop.kotlin.plugin.SingleClick * *(..))")
    fun methodAnnotated() {

    }

   /**
    * joinPoint.proceed() 执行注解所标识的代码
    * @After 可以在方法前插入代码
    * @Before 可以在方法后插入代码
    * @Around 可以在方法前后各插入代码
    */
    @Around("methodAnnotated()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint){
       val methodSignature: MethodSignature = joinPoint.signature as MethodSignature
       //默认值是600毫秒
       val timeValue: SingleClick = methodSignature.method.getAnnotation(SingleClick::class.java)
       //获取系统当前时间
       val currentTime = Calendar.getInstance().timeInMillis
       //当前时间-上次记录时间>过滤的时间 过滤掉600毫秒内的连续点击
       //表示该方法可以执行
       if (currentTime - lastClickTime > timeValue.timeInterval) {
           if(BuildConfig.DEBUG_LOG){
               Log.e(TAG, "currentTime:$currentTime")
           }
           //将刚进入方法的时间赋值给上次点击时间
           lastClickTime = currentTime
           //执行原方法
           joinPoint.proceed()
       }
       lastClickTime =currentTime
    }

    companion object {
        const val TAG ="SingleClickAspect"
        var lastClickTime = 0L
    }
}
