# 支持的参数变量

## 全部参数变量

> 可用使用参数来完成`pipeline`的定制配置，下表列出了支持的所有参数变量。

### 1. 运行参数

|       标识        |  名称  |    类型     | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:---------------:|:----:|:---------:|:----------------:|:----------------:|:----------------:|
| [debug](#debug) | 调试模式 | `Boolean` |        ✅         |        ✅         |        ✅         |

### 2. 项目信息

|              标识               |   名称   |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------:|:------:|:--------:|:----------------:|:----------------:|:----------------:|
|  [projectName](#projectName)  |  项目名称  | `String` |        ✅         |        ✅         |        ✅         |
| [projectTitle](#projectTitle) |  项目标题  | `String` |        ✅         |        ✅         |        ✅         |
|   [projectUrl](#projectUrl)   | 项目访问地址 | `String` |        ✅         |        ✅         |        ✅         |

### 3. 构建参数

|                 标识                  |     名称      |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------------:|:-----------:|:--------:|:----------------:|:----------------:|:----------------:|
| [pipelineRunNode](#pipelineRunNode) |    运行节点     | `String` |        ✅         |        ✅         |        ✅         |
|      [gitCodeUrl](#gitCodeUrl)      |   项目主仓库地址   | `String` |        ✅         |        ✅         |        ✅         |
|   [gitCodeBranch](#gitCodeBranch)   | 项目主仓库默认构建分支 | `String` |        ✅         |        ✅         |        ✅         |
| [cleanCacheValue](#cleanCacheValue) |  清理缓存默认方式   | `String` |        ✅         |        ✅         |        ✅         |
|  [cleanCachePath](#cleanCachePath)  |  清理编译缓存路径   | `String` |        ✅         |        ✅         |        ✅         |
|      [gitCodeSub](#gitCodeSub)      |   项目子仓库列表   | `String` |        ✅         |        ✅         |        ✅         |

### 3. 编译打包配置

|                标识                 |       名称       |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:---------------------------------:|:--------------:|:--------:|:----------------:|:----------------:|:----------------:|
|   [buildJdkTool](#buildJdkTool)   |    jdk打包工具     | `String` |        ✅         |        ✅         |        ❌         |
| [buildMavenTool](#buildMavenTool) |   maven编译工具    | `String` |        ✅         |        ✅         |        ❌         |
|  [buildNodeTool](#buildNodeTool)  |    NODE编译工具    | `String` |        ❌         |        ❌         |        ✅         |
|   [buildCommand](#buildCommand)   |     编译打包命令     | `String` |        ✅         |        ✅         |        ✅         |
|     [targetPath](#targetPath)     | 构建产物（打包输出）所在路径 | `String` |        ✅         |        ✅         |        ✅         |

### 4. 运行部署配置

|              标识               |    名称    |     类型      | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------:|:--------:|:-----------:|:----------------:|:----------------:|:----------------:|
| [deployServer](#deployServer) |  部署服务器   | `List<Map>` |        ❌         |        ✅         |        ✅         |
|    [runEnvKey](#runEnvKey)    | 容器运行环境键  |  `String`   |        ❌         |        ✅         |        ✅         |
|    [deployEnv](#deployEnv)    |   部署环境   |  `String`   |        ❌         |        ✅         |        ✅         |
|   [deployPort](#deployPort)   |  部署对外端口  |  `Integer`  |        ❌         |        ✅         |        ✅         |
|      [runPort](#runPort)      |   运行端口   |  `Integer`  |        ❌         |        ✅         |        ✅         |
|   [runNetwork](#runNetwork)   | 容器运行网络模式 |  `String`   |        ❌         |        ✅         |        ✅         |
|   [runRestart](#runRestart)   |  容器重启策略  |  `String`   |        ❌         |        ✅         |        ✅         |

### 5. 构建选项配置

|                       标识                        |    名称    |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------------------------:|:--------:|:--------:|:----------------:|:----------------:|:----------------:|
|          [buildTimeOut](#buildTimeOut)          |  构建超时时间  | `String` |        ✅         |        ✅         |        ✅         |
|         [daysToKeepStr](#daysToKeepStr)         |  构建保留天数  | `String` |        ✅         |        ✅         |        ✅         |
|          [numToKeepStr](#numToKeepStr)          |  构建保留个数  | `String` |        ✅         |        ✅         |        ✅         |
| [artifactDaysToKeepStr](#artifactDaysToKeepStr) | 构建产物保留天数 | `String` |        ✅         |        ✅         |        ✅         |
|  [artifactNumToKeepStr](#artifactNumToKeepStr)  | 构建产物保留个数 | `String` |        ✅         |        ✅         |        ✅         |

### 6. 凭证信息

|              标识               |      名称       |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------:|:-------------:|:--------:|:----------------:|:----------------:|:----------------:|
|  [gitCodeAuth](#gitCodeAuth)  |   git代码仓库凭证   | `String` |        ✅         |        ✅         |        ✅         |
| [registryAuth](#registryAuth) | Registry服务器凭证 | `String` |        ✅         |        ✅         |        ✅         |

### 7. 镜像Registry配置

|                   标识                    |         名称         |       类型       | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:---------------------------------------:|:------------------:|:--------------:|:----------------:|:----------------:|:----------------:|
| [imagePushRegistry](#imagePushRegistry) | 是否推送镜像到Registry服务器 |   `Boolean`    |        ❌         |        ✅         |        ✅         |
|       [registryUrl](#registryUrl)       |     Registry地址     |    `String`    |        ❌         |        ✅         |        ✅         |
|    [imagePullLogin](#imagePullLogin)    |     拉取镜像是否需要登录     |   `Boolean`    |        ❌         |        ✅         |        ✅         |
|   [registryProject](#registryProject)   |     Registry项目     |    `String`    |        ❌         |        ✅         |        ✅         |
| [imageTempSavePath](#imageTempSavePath) |      镜像临时保存路径      |    `String`    |        ❌         |        ✅         |        ✅         |
|         [baseImage](#baseImage)         |        基础镜像        | `List<String>` |        ❌         |        ✅         |        ✅         |


### 8. 通知配置

|               标识                |   名称    |    类型    | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-------------------------------:|:-------:|:--------:|:----------------:|:----------------:|:----------------:|
|    [notifyType](#notifyType)    |  通知方式   | `String` |        ✅         |        ✅         |        ✅         |
| [notifyRobotId](#notifyRobotId) | 通知机器人ID | `String` |        ✅         |        ✅         |        ✅         |

### 9. 通知配置

|                       标识                        |    名称    |    类型     | javaPublishMaven | javaDeployDocker | nodeDeployDocker |
|:-----------------------------------------------:|:--------:|:---------:|:----------------:|:----------------:|:----------------:|
| [stageArchiveArtifacts](#stageArchiveArtifacts) | 是否开启构建产物 | `Boolean` |        ✅         |        ✅         |        ✅         |
|    [stageMessageNotify](#stageMessageNotify)    | 是否开启通知消息 | `Boolean` |        ✅         |        ✅         |        ✅         |

## 参数变量详解

### <a id="debug">`debug`</a>

- 类型：`Boolean`
- 默认值：`false`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 调试模式，`false`会最大限度的减少输出日志

### <a id="projectName">`projectName`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目名称，镜像、容器名称，默认`Jenkins`任务名称，建议规则：项目名称-端类型`[siweite-web]`

### <a id="projectTitle">`projectTitle`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目标题，项目的中文名称，用于消息通知展示

### <a id="projectUrl">`projectUrl`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目访问地址，用于消息通知展示

### <a id="pipelineRunNode">~~`pipelineRunNode`~~</a>

- 类型：`String`
- 默认值：`any`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> **TODO 目前不支持配置** 运行节点，项目构建运行的计算机节点

### <a id="gitCodeUrl">`gitCodeUrl`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目主仓库地址，构建项目的根代码仓库地址

### <a id="gitCodeBranch">`gitCodeBranch`</a>

- 类型：`String`
- 默认值：`main`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目主仓库默认构建分支

### <a id="cleanCacheValue">`cleanCacheValue`</a>

- 类型：`String`
- 默认值：`no_clean`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 清理缓存默认方式，可选：`no_clean` `clean_build` `clean_workspace` `clean_all`
> 
> `no_clean`：不清理缓存和构建资源
> 
> `clean_build`：清理构建资源，如：`Java`清理`Maven`本地仓库、`node`清理`node_model`
> 
> `clean_workspace`：删除当前工作空间所有内容
> 
> `clean_all`：清理包括`clean_build` `clean_workspace`

### <a id="cleanCachePath">`cleanCachePath`</a>

- 类型：`String`
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 清理构建缓存路径（相对于Maven本地仓库/Node的node_modules的相对路径）
> 
> 如：
> 
> Java相关模板：`maven`的本地仓库为`/data/maven/repo`，清理路径为`/com/siweite`，实际清理路径为`/data/maven/repo/com/siweite`
> 
> Node相关模板：清理路径为`/.cache`，实际清理路径为当前项目的`node_modules/.cache`

### <a id="gitCodeSub">`gitCodeSub`</a>

- 类型：`List<Map>`
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 项目子仓库列表，将子代码仓库列表，合并到主项目中进行编译/打包
> 
> `targetDir`：拉取的子项目仓库代码存放路径（相对于主项目路径）
> 
> `url`：子项目仓库地址
> 
> `branch`：子项目拉取的分支，不传或为空时默认使用主项目分支

```groovy
[
    [
        'targetDir': 'view/siweite', 
        'url': 'http://test.siweite.top/demo.git', 
        'branch': 'main'
    ]
]
```

### <a id="buildJdkTool">`buildJdkTool`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker`

> jdk打包工具，配置名称：Jenkins → 系统管理 → 全局工具配置 → JDK安装 → 别名

### <a id="buildMavenTool">`buildMavenTool`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker`

> maven编译工具，配置名称：Jenkins → 系统管理 → 全局工具配置 → Maven安装 → 别名

### <a id="buildNodeTool">`buildNodeTool`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`nodeDeployDocker`

> NODE编译工具，配置名称：Jenkins → 系统管理 → 全局工具配置 → NodeJS安装 → 别名

### <a id="buildCommand">`buildCommand`</a>

- 类型：`String`
- 默认值：
  - `javaPublishMaven`：`mvn clean package`
  - `javaDeployDocker`：`mvn clean package`
  - `nodeDeployDocker`：`npm run build`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 编译打包命令，多行命令使用 \n 分割 或 使用 && 或者使用 三引号，推荐使用**三引号**(和实际编写命令一致)

### <a id="targetPath">`targetPath`</a>

- 类型：`String`
- 默认值：无
- 必填：
    - `javaPublishMaven`：`否`
    - `javaDeployDocker`：`是`
    - `nodeDeployDocker`：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 构建产物（打包输出）所在路径，支持目录、文件（相对于项目的路径）,会自动将产物移动到`Jenkinsfile`所在目录，方便制造镜像使用
> 
> 如果开启构建产物(stageArchiveArtifacts)，则该参数必填

### <a id="deployServer">`deployServer`</a>

- 类型：`List<String>`
- 默认值：无
- 必填：`是`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 部署服务器(第一个为默认值)，配置名称：Jenkins → 系统管理 → 系统配置 → SSH Servers → Name

```groovy
    ['server1', 'server2', 'server3']
```

### <a id="runEnvKey">`runEnvKey`</a>

- 类型：`String`
- 默认值：`SPRING_PROFILES_ACTIVE`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 容器运行环境键，只能添加一个环境变量key=配置值，value=DEPLOY_ENV配置值，为了一条流水线支持多环境部署
> 
> 例如配置：SPRING_PROFILES_ACTIVE，动态控制SpringBoot运行环境

### <a id="deployEnv">`deployEnv`</a>

- 类型：`String`
- 默认值：无
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 部署环境，写入容器启动时环境变量（`key`：`RUN_ENV_KEY`配置的值），影响打包、启动读取的环境配置，不配置，则不会添加环境变量
> 
> 例如：配`runEnvKey`=`SPRING_PROFILES_ACTIVE`、就可以动态控制`SpringBoot`读取的配置文件

### <a id="deployPort">`deployPort`</a>

- 类型：`Integer`
- 默认值：无
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 部署默认端口，对外可访问端口，非容器内部端口。在流水线构建时还可以手动修改
> 
> 如果容器运行网络模式`RUN_NETWORK`=`bridge`, 则该参数为必填，也可以不配置，在构建时手动输入

### <a id="runPort">`runPort`</a>

- 类型：`Integer`
- 默认值：
  - `javaDeployDocker`：`8080`
  - `nodeDeployDocker`：`80`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 运行端口，容器内部访问端口

### <a id="runNetwork">`runNetwork`</a>

- 类型：`String`
- 默认值：`bridge`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 网络模式，容器运行的网络模式，可选：`bridge` `host` `none` `overlay` 详解：[网络驱动模式](https://docs.docker.com/engine/network/drivers/)
>
> 当等于`bridge`时，`deployPort`和`runPort`两参数必填

### <a id="runRestart">`runRestart`</a>

- 类型：`String`
- 默认值：`no`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 容器重启策略，可选：`no` `on-failure` `always` `unless-stopped` 详解：[容器自动启动策略](https://docs.docker.com/engine/containers/start-containers-automatically/")

### <a id="buildTimeOut">`buildTimeOut`</a>

- 类型：`String`，只能支持数字整数
- 默认值：`60`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 构建超时时间，默认60分钟，针对于单个步骤的构建超时时间

### <a id="daysToKeepStr">`daysToKeepStr`</a>

- 类型：`String`，只能支持数字整数
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 丢弃旧的构建，构建保留此天数，为空保留天数不限制

### <a id="numToKeepStr">`numToKeepStr`</a>

- 类型：`String`，只能支持数字整数
- 默认值：`15`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 丢弃旧的构建，构建保留个数，为空保留个数不限制，默认保留15个

### <a id="artifactDaysToKeepStr">`artifactDaysToKeepStr`</a>

- 类型：`String`，只能支持数字整数
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 丢弃旧的构建，构建产物保留天数，为空保留天数不限制

### <a id="artifactNumToKeepStr">`artifactNumToKeepStr`</a>

- 类型：`String`，只能支持数字整数
- 默认值：`15`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 丢弃旧的构建，构建产物保留个数，为空保留个数不限制，默认保留15个

### <a id="gitCodeAuth">`gitCodeAuth`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> git代码仓库凭证，用于拉取待构建的代码仓库、pipeline脚本仓库

### <a id="registryAuth">`registryAuth`</a>

- 类型：`String`
- 默认值：无
- 必填：`否`
  - `imagePushRegistry`=`true`：`是`
  - `imagePullLogin`=`true`：`是`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> Registry服务器（例如：`Harbor`）凭证，用于登录后拉取、推送镜像

### <a id="imagePushRegistry">`imagePushRegistry`</a>

- 类型：`Boolean`
- 默认值：`false`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 是否推送镜像到Registry服务器（`true`：`registryAuth` `registryUrl` 必填；`false`：`imageTempSavePath` 必填）
> 
> 开启：构建成功后，会自动将构建产物镜像推送到Registry服务器，部署会从Registry服务器拉取镜像进行部署
> 
> 关闭：构建成功后，会直接把镜像包上传到部署服务器进行部署

### <a id="registryUrl">`registryUrl`</a>

- 类型：`String`
- 默认值：无
- 必填：`否`
  - `imagePushRegistry`=`true`：`是`
  - `imagePullLogin`=`true`：`是`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> Docker镜像Registry服务器地址，用于登录Registry服务器、推送镜像
> 
> 注意：不能带`http://`、`https://`前缀

### <a id="imagePullLogin">`imagePullLogin`</a>

- 类型：`Boolean`
- 默认值：`false`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 拉取镜像是否需要登录（`true`：`registryAuth` `registryUrl` 必填）

### <a id="registryProject">`registryProject`</a>

- 类型：`String`
- 默认值：无
- 必填：`是`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> Registry服务器项目，用于推送镜像到Registry项目

### <a id="imageTempSavePath">`imageTempSavePath`</a>

- 类型：`String`
- 默认值：`siweite-pipeline-image`
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 远端部署服务器镜像临时保存相对路径（当`imagePushRegistry`=`false`时使用）
>
> 基于选择部署的服务器配置的远端路径(Jenkins -> 系统管理 -> 系统配置 -> SSH Servers -> Name)的相对路径
> 
> 会将打包好的镜像文件推送到远端部署服务器，构建成功后，会自动删除

### <a id="baseImage">`baseImage`</a>

- 类型：`List<String>`
- 默认值：无
- 必填：`否`
- 支持模板：`javaDeployDocker` `nodeDeployDocker`

> 需要使用到的基础镜像，只有新版Docker-Engine才需要配置
> 
> 由于新版Docker的Dockerfile使用From做基础镜像必须是https，就需要提前拉取测试强制需要https的版本（Docker version 27.4.1以上），其他版本是否强制需要https，请自行测试
> 
> 方式一：填写此字段，流水线自动拉取填写的镜像（适合无法操作流水线服务器）[推荐]
> 
> 方式二：手动在流水线 制作镜像的服务器上拉取需要的镜像（一般来说是流水线服务器和制作镜像的服务器是同一个）

### <a id="notifyType">`notifyType`</a>

- 类型：`List<String>`
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 通知方式，可选：`Feishu` `DingTalk` `QyWechat` **TODO 暂不支持企业微信**

### <a id="notifyRobotId">`notifyRobotId`</a>

- 类型：`String`
- 默认值：无
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 通知机器人id，开启构建消息通知(stageMessageNotify)，则该参数必填

### <a id="stageArchiveArtifacts">`stageArchiveArtifacts`</a>

- 类型：`boolean`
- 默认值：`false`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 是否开启构建产物-步骤（开启后，支持下载打包后的产物）

### <a id="stageMessageNotify">`stageMessageNotify`</a>

- 类型：`boolean`
- 默认值：`false`
- 必填：`否`
- 支持模板：`javaPublishMaven` `javaDeployDocker` `nodeDeployDocker`

> 是否开启通知消息-步骤（开启后，流水线失败或成功都会发送通知消息）