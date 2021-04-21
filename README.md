![xx](banner.png)
# 黑盒 · BlackBox

![](https://img.shields.io/badge/language-java-brightgreen.svg)

黑盒BlackBox，是一款虚拟引擎，可以在Android上克隆、运行虚拟应用，拥有免安装运行能力。黑盒可以掌控被运行的虚拟应用，做任何想做的事情。

## 项目声明
- 本项目为个人**学习项目**。有重复造轮的嫌疑，许多设计细节有参考[VirtualApp](https://github.com/asLody/VirtualApp)、[VirtualAPK](https://github.com/didi/VirtualAPK)、许多资料和文章。本项目部分工具类来自VirtualApp，例如mirror、小部分Utils。author注释不修改，永久保留在项目内，感谢作者无私奉献。

- 但是在此声明，本项目**并非CV项目**，项目内每一行代码都是亲自手敲下，深入至所有核心原理。

- **本项目(app)仅为测试demo，用于初期测试稳定性。后续可能考虑开发成黑科技盒子之类的应用。**
## 反馈
**issue 或者 QQ群：970690922**

## 支持
暂不考虑4x，目前已兼容 5.0 ～ 11.0并跟进后续新系统 由于个人资源有限设备有限，测试范围比较小，目前正在计划用在自己或朋友项目上进行线上测试完善此项目。同时也欢迎兄弟们疯狂issue一起完善。目前经水友测试许多常规应用均可运行（能否完美使用，此处留个问号），相对传统插件化功能此项目完全够用，但是作为双开虚拟引擎，他需要做的是相对完整的生态系统，需要做的还有很多。

#### 以下是他行我不行系列
应用 | 问题 | 然后呢
---|---|---
QQ | 无网 | 仍在努力中，可能姿势不对

## 如何使用
#### Step 1.初始化，在Application中加入以下代码初始化

```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
    }
```

#### Step 2.运行APK
运行未安装应用，只需提供一个可访问的APK路径
```java
    try {
        BlackBoxCore.get().launchApk(new File("/sdcard/123.apk"));
    } catch (RuntimeException e) {
        e.printStackTrace();
        // 当前架构不支持运行此APP
    }
```

运行已安装应用提供包名即可
```java
    try {
        BlackBoxCore.get().launchApk("com.tencent.mm");
    } catch (RuntimeException e) {
        e.printStackTrace();
        // 当前架构不支持运行此APP
    }
```

### 架构不支持特别说明
由于Android系统的原因，当前进程以arm64-v8a启动，无法再运行armeabi-v7a的so库。所以如果宿主是arm64-v8a，则无法双开运行armeabi-v7a架构的APP，需要切换宿主的架构。后续会参考市面上双开的做法，集成64位或32位插件版。

## 计划
 - 多用户态、目前仅支持双开，但是所有细节均有预留。
 - 应用安装，目前仅有启动一说，只需提供Apk路径（未安装）或者包名（已安装）即可启动虚拟应用，类似于VirtualApk的loadPlugin，重启后需要重新load。原因是先想做成方便插件化引入项目线上进行测试。
 - vpid回收再使用（目前getAndIncrement，因为有100个坑位也用不完，所以暂时怎么方便怎么来）
 - PaddingIntent
 - JobService调度可优化，但是目前仍然采取占坑。
 - Service API 虚拟化（目前许多是使用系统API，只有少数已实现）
 - 太多了我靠，慢慢来吧。
 

### 一些说明
 - IO的重定向目前做在Java，对MountService、OS、ApplicationInfo等进行了Hook，基本满足绝大部分应用。暂时未做底层的重定向，因为Java更稳定且轻松。
 - Activity栈的处理，目前看着好像没问题但是总感觉哪里有问题，但是可以肯定的是肯定有问题，只是没发现。
 - 已内置XP环境。
 - 已支持GG修改器

## 一些额外的话
想要做成一个非常理想的状态需要花费大量时间，由于自己时间精力有限，独乐乐不如众乐乐。**欢迎大家来PR，一起造轮**。到目前为止还有许多功能暂未支持，ShortcutManager、NotificationManager等等诸如此类还有非常多无法一一列举。目前是以跑起来就行的目标去前进，更多完善功能在后续会慢慢填补。
