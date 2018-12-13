package aop.kotlin.plugin.aspectj

import aop.kotlin.plugin.utils.LoginHandlerInterceptor
import aop.kotlin.plugin.annotation.LoginInterceptor
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect
internal class LoginInterceptorAspect {

    @Pointcut("execution(@aop.kotlin.plugin.annotation.LoginInterceptor * *(..))")
    fun loginPointIntercept(){
    }

    @Around("loginPointIntercept()")
    @Throws(Throwable::class)
    fun aroundLoginPoint(joinPoint: ProceedingJoinPoint) {
        val listener = LoginHandlerInterceptor.instance.iLoginListener
        val methodSignature = joinPoint.signature
        if(null!=listener && null!=methodSignature && methodSignature is MethodSignature){
            //该注解只能用于方法上
            val loginInterceptor=methodSignature.method.getAnnotation(LoginInterceptor::class.java)
            if (null!=loginInterceptor){
                if(listener.isLogin()){
                    joinPoint.proceed()
                }else if(!listener.onInterceptor(loginInterceptor.interceptorType)){
                    joinPoint.proceed()
                }
            }
        }
    }
}