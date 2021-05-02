![xx](banner.png)
# 黑盒 · BlackBox

![](https://img.shields.io/badge/language-java-brightgreen.svg)

黑盒BlackBox，是一款虚拟引擎，可以在Android上克隆、运行虚拟应用，拥有免安装运行能力。黑盒可以掌控被运行的虚拟应用，做任何想做的事情。

## 项目声明
- 本项目为个人**学习项目**。有重复造轮的嫌疑，许多设计细节有参考[VirtualApp](https://github.com/asLody/VirtualApp)、[VirtualAPK](https://github.com/didi/VirtualAPK)、许多资料和文章。本项目部分工具类来自VirtualApp，例如mirror、小部分Utils。author注释不修改，永久保留在项目内，感谢作者无私奉献。

- 但是在此声明，本项目**并非CV项目**，项目内每一行代码都是亲自手敲下，深入至所有核心原理。

- app模块由 [@楷城同学](https://github.com/wukaicheng) 完成
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

#### Step 2.安装应用至黑盒内
```java
    // 已安装的应用可以提供包名
    BlackBoxCore.get().installPackageAsUser("com.tencent.mm", userId);
    
    // 未安装的应用可以提供路径
    BlackBoxCore.get().installPackageAsUser(new File("/sdcard/com.tencent.mm.apk"), userId);
```

#### Step 2.运行黑盒内的应用
```java
   BlackBoxCore.get().launchApk("com.tencent.mm", userId);
```

### 相关API
#### 获取黑盒内已安装的应用
```java
   // flgas与常规获取已安装应用保持一致即可
   BlackBoxCore.get().getInstalledApplications(flgas, userId);
   
   BlackBoxCore.get().getInstalledPackages(flgas, userId);
```

#### 获取黑盒内的User信息
```java
   List<BUserInfo> users = BlackBoxCore.get().getUsers();
```

## 对XPoesd的支持
> 基于SandHook

#### 启用 / 禁用XPoesd
```java
   boolean isEnable = BlackBoxCore.get().isXPEnable();
   
   BlackBoxCore.get().setXPEnable(true);
```

#### 安装 / 卸载XP模块
```java
   BlackBoxCore.get().installXPModule(new File("/sdcard/module.apk"));
   
   BlackBoxCore.get().uninstallXPModule("com.xxx");
```

#### 启用 / 禁用模块
```java
   boolean isEnable = BlackBoxCore.get().isModuleEnable("com.xxx");
   
   BlackBoxCore.get().setModuleEnable("com.xxx", true);
```
#### 启用XPoesd模块界面
```java
   BlackBoxCore.get().launchXPModule("com.xxx");
```

#### 获取已安装的XPoesd模块列表
```java
   List<InstalledModule> installedModules = BlackBoxCore.get().getInstalledXPModules();
```

#### 更多其他操作看BlackBoxCore函数名大概就知道了。

### 架构不支持特别说明
由于Android系统的原因，当前进程以arm64-v8a启动，无法再运行armeabi-v7a的so库。所以如果宿主是arm64-v8a，则无法双开运行armeabi-v7a架构的APP，需要切换宿主的架构。后续会参考市面上双开的做法，集成64位或32位插件版。

## 计划
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

### License

> ```
> Copyright 2021 Milk
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
