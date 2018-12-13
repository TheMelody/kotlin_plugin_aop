package aop.kotlin.plugin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import ezy.assist.compat.SettingsCompat
import java.lang.Exception


object PermissionUtils {

    private const val TAG = "PermissionUtils"

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 检查权限列表中"未许可的权限列表"
     */
    fun getLackedPermissionList(context: Context?,permissions:Array<String>):MutableList<String>{
        val lackedPermission:MutableList<String> = mutableListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.let {
                for(permission in permissions){
                    if (checkSelfPermission(it,permission) != PackageManager.PERMISSION_GRANTED) {
                        lackedPermission.add(permission)//将未授权的权限添加到 "未许可的权限列表里"
                    }
                }
            }
        }
        return lackedPermission
    }

    /**
     * 获取当前权限key对应的中文|英文显示的内容
     */
    fun getPermissionName(context: Context?, permission:String):String{
        return context?.let {
            it.packageManager.let { packageManager ->
                packageManager.getPermissionInfo(permission,PackageManager.GET_META_DATA)?.loadLabel(packageManager).toString()
            }
        }.toString()
    }


    /**
     * Fragment请求权限
     */
    fun requestPermissions(context:Fragment?,permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //防止:在Fragment中申请运行时权限onRequestPermissionsResult收不到数据
            context?.requestPermissions(permissions,requestCode)
        }
    }

    /**
     * Activity请求权限
     */
    fun requestPermissions(context:Activity?,permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.requestPermissions(permissions,requestCode)
        }
    }

    /**
     * 跳转到应用权限设置页
     */
    fun startAppPermissionActivity(context: Context){
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        try {
            intent.data = Uri.parse("package:" + context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }catch (exception:Exception){
            Log.e(TAG,"startAppPermissionActivity(context: Context):$exception")
            //打开应用设置界面
            intent = Intent(Settings.ACTION_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun getContext(target: Any?): Context? {
        if(target is Fragment){
            return target.context
        }else if(target is Activity){
            return target
        }
        Log.e(TAG,"getContext(target: Any?)==null\nCause by: arg[target!=android.support.v4.app.Fragment||target!=android.app.Activity]")
        return null
    }

    /**
     * 检测是否授权悬浮窗权限了,true授权了,false没有授权
     */
    fun canDrawOverlays(context: Context):Boolean{
        return SettingsCompat.canDrawOverlays(context)
    }

    /**
     * 跳转到悬浮窗授权界面
     */
    fun openDrawOverlaysActivity(context: Context){
        SettingsCompat.manageDrawOverlays(context)
    }
}