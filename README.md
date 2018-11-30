# Kotlin版本的Aspectj切面玩法
:bowtie: [我的知乎](https://www.zhihu.com/people/qiang-fu-5-67/activities)  :paw_prints:

我们先介绍如何集成进我们的项目中，然后再介绍如何使用
------
#### 1.project root的src下面的build.gradle中配置plugin

```
 repositories {
        maven {
            url "https://jitpack.io"
        }
       //等等更多其他仓库地址,maven里面的这个地址是必须要的
 }
  dependencies {
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
 }
```

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
implementation 'aop.kotlinx.plugin:kotlin_plugin_aop:1.0.0'
```
## :see_no_evil:前方高能，接下来我们就可以使用我们的aop插件了
目前我们提供四个功能：

* [1.防止控件重复点击](#1)
* [2.开发模式下,打印方法耗时](#2)
* [3.运行时权限申请](#3)
* [4.悬浮窗权限申请](#4)

<span id="#1">1.防止重复点击</span>









