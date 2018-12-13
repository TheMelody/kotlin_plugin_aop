package aop.kotlin.plugin.inf

interface ILoginListener {
    fun onInterceptor(interceptorType:Int):Boolean
    fun isLogin():Boolean
}