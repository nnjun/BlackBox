![xx](banner.png)
# 黑盒 · BlackBox

![](https://img.shields.io/badge/language-java-brightgreen.svg)

黑盒BlackBox，是一款虚拟引擎，可以在Android上克隆、运行虚拟应用，拥有免安装运行能力。黑盒可以掌控被运行的虚拟应用，做任何想做的事情。

## 项目声明
- 本项目为个人**学习项目**。有重复造轮的嫌疑，许多设计细节有参考[VirtualApp](https://github.com/asLody/VirtualApp)、[VirtualAPK](https://github.com/didi/VirtualAPK)、许多资料和文章。本项目部分工具类来自VirtualApp，例如mirror、小部分Utils。author注释不修改，永久保留在项目内，感谢作者无私奉献。

- 但是在此声明，本项目**并非CV项目**，项目内每一行代码都是亲自手敲下，深入至所有核心原理。

- app模块由 [@楷城同学](https://github.com/wukaicheng) 完成
## 问题反馈
**issue 或者 QQ群：970690922**，由于更新迭代飞快，编译成品会放在群里。

## 支持
暂不考虑4x，目前已兼容 5.0 ～ 12.0并跟进后续新系统 由于个人资源有限设备有限，测试范围比较小，目前正在计划用在自己或朋友项目上进行线上测试完善此项目。同时也欢迎兄弟们疯狂issue一起完善。目前经水友测试许多常规应用均可运行（能否完美使用，此处留个问号），相对传统插件化功能此项目完全够用，但是作为双开虚拟引擎，他需要做的是相对完整的生态系统，需要做的还有很多。

### 问题应用
应用 | 问题 | 然后呢
---|---|---
QQ | 无网 | 仍在努力中，可能姿势不对

## 架构特别说明
本项目区分32位与64位，目前是2个不同的app，如在Demo已安装列表内无法找到需要开启的app说明不支持，请编译其他的架构。

## 如何使用
### Step 1.初始化，在Application中加入以下代码初始化

```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
            });
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

### Step 2.安装应用至黑盒内
```java
    // 已安装的应用可以提供包名
    BlackBoxCore.get().installPackageAsUser("com.tencent.mm", userId);

    // 未安装的应用可以提供路径
    BlackBoxCore.get().installPackageAsUser(new File("/sdcard/com.tencent.mm.apk"), userId);
```

### Step 2.运行黑盒内的应用
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
更多其他操作看BlackBoxCore函数名大概就知道了。


#### Xposed相关
- 已支持使用XP模块
- Xposed已粗略过检测，[Xposed Checker](https://www.coolapk.com/apk/190247)、[XposedDetector](https://github.com/vvb2060/XposedDetector) 均无法检测


## 如何参与开发？
### 应用分2个模块
- app模块，用户操作与UI模块
- Bcore模块，此模块为BlackBox的核心模块，负责完成整个黑盒的调度。

如需要参与开发请直接pr就可以了，相关教程请百度或者看 [如何在 GitHub 提交第一个 pull request](https://chinese.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github/)
### PR须知
1. 中英文说明都可以，但是一定要详细说明问题
2. 请遵从原项目的代码风格、设计模式，请勿个性化。
3. PR不分大小，有问题随时欢迎提交。

## 计划
 - 更多的Service API 虚拟化（目前许多是使用系统API，只有少数已实现）
 - 提供更多接口给开发者（虚拟定位、应用注入等）

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
