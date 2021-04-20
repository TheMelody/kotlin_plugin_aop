# Kotlin版本的Aspectj切面玩法 :see_no_evil: 
<img src="https://raw.githubusercontent.com/TheMelody/3DEmoji/master/mogutou_test.gif" width="66.66"/>[我的知乎](https://www.zhihu.com/people/qiang-fu-5-67/activities)  [![我的知乎](https://github.com/TheMelody/3DEmoji/blob/master/html-element.svg)](https://www.zhihu.com/people/qiang-fu-5-67/activities)


我们先介绍如何集成进我们的项目中，然后再介绍如何使用
------
首先我们需要把[version.gradle](https://raw.githubusercontent.com/TheMelody/kotlin_plugin_aop/master/version.gradle)
(公共版本配置)粘贴在project的根目录

#### 1.project root的src下面的build.gradle中配置plugin

```
我们在root的build.gradle文件内容的最上层增加
apply from: 'version.gradle'

然后在buildscript内部增加下面的内容:
 repositories {
        maven {
            url "https://jitpack.io"
        }
       //等等更多其他仓库地址,maven里面的这个地址是必须要的
 }
  dependencies {
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.10"
    classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
 }
```
#### 增加了version.gradle之后你们如果想要修改kotlin和aspectj的版本都可以在version.gradle中修改了

#### 2.app下面的build.gradle中配置plugin
```
apply plugin: 'kotlin-android'

apply plugin: 'kotlin-android-extensions'

apply plugin: 'android-aspectjx'
```
#### 这三个plugin需要配置在默认的plugin下面

- [x] apply plugin: 'com.android.application' 

接下来配置aspectj需要过滤的其他库，具体可以参考Aspectj语法，默认不做任何配置的话，
会遍历项目编译后所有的 .class 文件和依赖的第三方库进行处理。我们通过exclude过滤缩小范围
```
android {
  aspectjx {
        exclude 'org.jetbrains.kotlin'
        exclude 'org.jetbrains.kotlinx'
        exclude 'com.android.support'
    }
}
```

#### 3.build.gradle依赖远程libary
```
implementation 'aop.kotlinx.plugin:kotlin_plugin_aop:1.0.6'
```
## :see_no_evil:前方高能，接下来我们就可以使用我们的aop插件了
目前我们提供四个功能：
****
* [1.防止控件重复点击](#防止控件重复点击)
* [2.开发模式下,打印方法耗时](#开发模式下-打印方法耗时)
* [3.运行时权限申请](#运行时权限申请)
* [4.悬浮窗权限申请](#悬浮窗权限申请)
* [5.登录拦截](#登录拦截)


### 防止控件重复点击
------
##### 第一种默认时间间隔600毫秒
```
@SingleClick
fun testSingleClick(){
   Log.d(TAG,"testSingleClick()")
}
```
##### 第二种自定义时间间隔，毫秒值
```
@SingleClick(timeInterval = 1000)
fun testSingleClick(){
   Log.d(TAG,"testSingleClick()")
}
```
### 开发模式下-打印方法耗时
------
```
@DebugTimeTrace(value = "登录")
fun doLogin(){
   //不可描述的耗时
}
```

### 运行时权限申请
------
```
@CheckPermission(requestCode = 1001, permissions = [Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA])
fun chooseAlbum(){
        //权限申请写和相机权限,样例
}
```
##### 我们需要在Activity或者Fragment中复写
```
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
```
我们需要在这个回调方法上面增加授权结果的注解 @OnRequestPermissionsResult
这个注解类默认的参数是false 
```
annotation class OnRequestPermissionsResult(val useDefaultDeniedDialog:Boolean = false)
```
如果我们配置成了true，则使用我们写好的默认权限拒绝的提示dialog，
##### 如果我们配置成了true，那么我们需要在res目录下的values.xml中
增加如下内容:
##### (如果你用默认的false，则不需要配置这三个string值)
```
<string name="dialog_aop_permission_title">提示</string>
<string name="dialog_aop_permission_message">当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开如下权限:</string>
<string name="dialog_aop_permission_message_no_details">当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需的权限。</string>
```
如果你配置成了true那么你只需在回调上方增加@OnRequestPermissionsResult(useDefaultDeniedDialog = true)就结束了
如果你使用默认的false，那么你需要自己处理权限拒绝之后的处理逻辑，如下方法demo：
```
@OnRequestPermissionsResult
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1001){//这个和你的请求的code需要匹配
            permissions.classCast<Array<String>>()?.let {
                PermissionUtils.getLackedPermissionList(this, it).let { lackedList ->
                   //不可描述......
                }
            }
        }
    }
    
对于上面的onRequestPermissionsResult()方法里面的, permissions.classCast<T>()的方法使用如下:

//防止类型擦除,reified具体化一个T的类型参数
inline fun <reified T> Any.classCast() : T? = this as? T?
    
```

### 悬浮窗权限申请
------
```
@CheckDrawOverLaysPermission
fun checkFloatWindow(){
   //悬浮窗权限有了,可以做下面的事情了
}
```
如果自己要控制悬浮窗检查可以使用下面两个方法：
```
检测是否授权悬浮窗权限了,true授权了,false没有授权
aop.kotlin.plugin.PermissionUtils#canDrawOverlays

跳转到悬浮窗授权界面
aop.kotlin.plugin.PermissionUtils#openDrawOverlaysActivity
```

### 登录拦截
------
我们在Application的onCreate中初始化拦截器
```
override fun onCreate() {
        super.onCreate()
        LoginHandlerInterceptor.instance.init(object : ILoginListener {
            override fun onInterceptor(interceptorType: Int): Boolean {//类型自己定义
                if(interceptorType==0){
                    //跳转到登录界面,弹dialog，自己随意
                    return true
                }else if(interceptorType==1){
                    Log.d(TAG,"不拦截,方法执行不受影响,打开其他界面,可以弹Toast")
                    return false
                }else if(interceptorType==2){
                    
                }else if(更多自己定义的类型){
                 
                }
                return true
            }
            override fun isLogin(): Boolean {
                return false //自己项目根据自己项目情况判断当前是否是登录状态
            }
        })
    }
```
onInterceptor方法如果返回true，注解的方法不会继续执行，如果返回false，方法继续执行不受影响。

#### 注解的用法如下：
```
@LoginInterceptor(interceptorType = 0)
fun testMethod(){
 //执行其他业务逻辑
 //interceptorType定义的类型，我们在Application中要对你定义的类型做拦截
}
```

## :clap: 用法全部介绍完，有新的想法可以提出来

# 写在最后,混淆配置

```
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }


-keep @aop.kotlin.plugin.annotation.CheckPermission class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.CheckPermission <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.CheckPermission <methods>;
}

-keep @aop.kotlin.plugin.annotation.OnRequestPermissionsResult class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.OnRequestPermissionsResult <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.OnRequestPermissionsResult <methods>;
}

-keep @aop.kotlin.plugin.annotation.SingleClick class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.SingleClick <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.SingleClick <methods>;
}

-keep @aop.kotlin.plugin.annotation.LoginInterceptor class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.LoginInterceptor <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.LoginInterceptor <methods>;
}
-keep @aop.kotlin.plugin.annotation.DebugTimeTrace class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.DebugTimeTrace <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.DebugTimeTrace <methods>;
}
-keep @aop.kotlin.plugin.annotation.CheckDrawOverLaysPermission class * {*;}
-keep class * {
    @aop.kotlin.plugin.annotation.CheckDrawOverLaysPermission <fields>;
}
-keepclassmembers class * {
    @aop.kotlin.plugin.annotation.CheckDrawOverLaysPermission <methods>;
}

```



