package aop.kotlin.plugin.utils

import aop.kotlin.plugin.inf.ILoginListener

class LoginHandlerInterceptor private constructor() {
    companion object {
        val instance: LoginHandlerInterceptor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LoginHandlerInterceptor()
        }
    }

    internal var iLoginListener: ILoginListener? = null

    fun init(listener: ILoginListener){
        iLoginListener=listener
    }

}