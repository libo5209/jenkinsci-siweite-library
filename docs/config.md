# 环境配置

> [!WARNING]
>
> 请确保你的Jenkins版本 >= 2.516.3
> 本文档只针对最新版本插件，请务必升级插件`Siweite Pipeline Template Plugin`

## 一 前置条件

> 安装依赖的`Jenkins`插件

| 标题                              | 描述                                                              | 是否必须                                |
|-----------------------------------|-------------------------------------------------------------------|-----------------------------------------|
| Pipeline                          | pipeline流水线插件                                                | 是                                      |
| build user vars plugin            | 获取构建用户环境变量                                              | 是                                      |
| Build Name and Description Setter | 设置构建显示名称和描述                                            | 是                                      |
| lark-notice                       | lark、飞书、钉钉、企微消息通知插件（支持企微最低插件版本: 2.1.9） | 否（启用消息通知-步骤，必须安装此插件） |
| Pipeline: Stage View              | pipeline阶段视图插件                                              | 否                                      |
| Pipeline Graph View               | pipeline流水线可视化插件                                          | 否                                      |

## 二 安装`Siweite Pipeline Template`插件

> `jenkinsci-siweite-library`共享库和`Siweite Pipeline Template Plugin`插件同步发版

安装对应的版本号版本 [插件地址](https://gitee.com/siweite/jenkinsci-siweite-library/releases/tag/0.5.2)

## 三 配置全局工具

> 按部署项目特性配置，一种工具支持配置多个版本

配置部署项目所依赖的基础工具，如：Jdk、Node、Maven等

配置位置：系统管理 → 全局工具配置 → JDK安装/Maven安装/NodeJS安装/

## 四 配置`SSH`远程服务器

> 配置需要远程部署服务器

### 4.1 配置`SSH`私钥
   - Jenkins所在服务器生成`SSH`私有和公钥
        ```sh
        ssh-keygen -t rsa
        ```
### 4.2 Jenkins配置私钥

配置位置：系统管理 → 系统配置 → Publish over SSH → Passphrase/Path to key/Key

#### 4.2.1 配置远程服务器

1. 复制公钥到远程服务器上

   ```sh
   ssh-copy-id 192.168.x.x
   ```

2. 添加需要部署的远程服务器

   配置位置：系统管理 → 系统配置 → Publish over SSH → SSH Servers（`Remote Directory`不能配置为`/`，会有“坑”）

   例如：
   > Name：app01
   > 
   > Hostname：192.168.x.x
   > 
   > Username：root

3. 测试配置是否成功

   `Test Configuration`

## 五 配置共享库

   配置位置：系统管理 → 系统配置 → Global Trusted Pipeline Libraries

   例如：
   > Name：jenkinsci-siweite-library
   > 
   > Default version：可以不配置，在使用共享库的时候指定分支名或标签
   > 
   > Retrieval method：Moderm SCM
   > 
   > Source Code Management：配置`jenkinsci-siweite-library`仓库地址（建议把仓库克隆到本地代码仓库中）
   > 
   > 建议：新增高级的克隆行为，配置浅克隆（深度 1 ），节约克隆时间
   > 
   > 其他配置保持默认即可

下一章：[搭建流水线](./pipeline.md)

