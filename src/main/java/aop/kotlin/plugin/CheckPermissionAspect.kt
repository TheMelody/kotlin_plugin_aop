package aop.kotlin.plugin

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import aop.kotlinx.plugin.R
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature

@Aspect
open class CheckPermissionAspect {

    private var mPermissionJoinPoint: ProceedingJoinPoint? = null

    @Pointcut("execution(@aop.kotlin.plugin.CheckPermission * *(..))")
    fun checkPermissionMethod() {
    }

    @Pointcut("execution(@aop.kotlin.plugin.OnRequestPermissionsResult * *(..))")
    fun requestPermissionsResult() {
    }

    @Around("checkPermissionMethod()")
    @Throws(Throwable::class)
    fun checkPermission(joinPoint: ProceedingJoinPoint) {
        mPermissionJoinPoint = joinPoint
        val target = joinPoint.target
        val signature :MethodSignature = joinPoint.signature as MethodSignature
        val checkPermission : CheckPermission = signature.method.getAnnotation(CheckPermission::class.java)
        val context:Context? = PermissionUtils.getContext(target)
        //未授权的权限列表
        val lackedPermission = PermissionUtils.getLackedPermissionList(context, checkPermission.permissions)
        if(lackedPermission.isEmpty()){
            //全部授权了
            try {
                joinPoint.proceed(joinPoint.args)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }else{
            //部分未授权的,这里区分Fragment和Activity
            //防止 >>>> 在Fragment中申请运行时权限onRequestPermissionsResult收不到数据!!!!
            if(target is Fragment){
                PermissionUtils.requestPermissions(target, lackedPermission.toTypedArray(), checkPermission.requestCode)
            }else if(target is Activity){
                PermissionUtils.requestPermissions(target, lackedPermission.toTypedArray(), checkPermission.requestCode)
            }
        }
    }

    /**
     * 在onRequestPermissionsResult方法里面的分支执行之前执行
     */
    @Before("requestPermissionsResult()")
    @Throws(Throwable::class)
    fun onRequestPermissionsResult(joinPoint: JoinPoint){
        val joinArgs = joinPoint.args
        //val requestCode: Int = joinArgs[0] as Int
        val permissions: Array<String>? = classCast<Array<String>>(joinArgs[1])
        val grantResults = joinArgs[2] as IntArray
        val signature :MethodSignature = joinPoint.signature as MethodSignature
        val permissionsResult : OnRequestPermissionsResult = signature.method.getAnnotation(OnRequestPermissionsResult::class.java)
        if (PermissionUtils.verifyPermissions(grantResults)) {
            //全部授权,当前方法可以继续执行
            mPermissionJoinPoint?.let { it.proceed(it.args) }
        }else{
            //没有授权或者部分权限没有授予
            if(permissionsResult.useDefaultDeniedDialog){
               //使用默认的拒绝提示框
               permissions?.let {it->
                   val lackedPermission = mPermissionJoinPoint?.let{point-> PermissionUtils.getContext(point.target) }?.let { context -> PermissionUtils.getLackedPermissionList(context, it) }
                   lackedPermission?.let {lackedList->
                       val context:Context? = PermissionUtils.getContext(mPermissionJoinPoint?.target)
                       var permissionNameList=""
                       for(permission in lackedList){
                           val permissionName:String= PermissionUtils.getPermissionName(context, permission)
                           if(permissionName != "null"){//防止系统没有给我们返回对应的权限名称内容
                               permissionNameList +=  "【$permissionName】,\n"
                           }
                       }
                       if(permissionNameList.length>2){
                           permissionNameList=permissionNameList.let { (it.substring(0,it.length-2)) }
                       }
                       if(lackedList.size>0){
                           context?.let {
                               var message = it.getString(R.string.dialog_aop_permission_message)+"\n\n$permissionNameList"
                               if(permissionNameList.isEmpty()){
                                   message=it.getString(R.string.dialog_aop_permission_message_no_details)
                               }
                               AlertDialog.Builder(it)
                                       .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                                           PermissionUtils.startAppPermissionActivity(it)
                                           dialogInterface.dismiss()
                                           mPermissionJoinPoint = null
                                       }.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                                           dialogInterface.dismiss()
                                           mPermissionJoinPoint = null
                                       }.setCancelable(false)
                                       .setTitle(it.getString(R.string.dialog_aop_permission_title))
                                       .setMessage(message).show()
                           }
                       }
                   }
               }
           }
        }
        mPermissionJoinPoint=null
    }

    /**
     * 防止类型擦除,reified具体化一个T的类型参数
     */
    private inline fun <reified T> classCast(any: Any?) : T? = any as? T?

}