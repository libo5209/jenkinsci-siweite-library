# Java远程Docker部署模板

## 支持的参数参数变量
> 可用使用参数来完成配置，下表概述了`Java发布私仓模板`所支持的参数

| 参数                      | 名称                 | 必填 | 类型      | 默认值          | 说明                                                         |
| ------------------------- | -------------------- | ---- | --------- | --------------- | ------------------------------------------------------------ |
| **运行参数**              |                      |      |           |                 |                                                              |
| DEBUG                     | 调试模式             | 否   | `Boolean` | `false`         | 是否开启调试模式，开启调试模式会最大程度打印日志，方便搭建流水线时排查和定位问题，**正式运行时，建议关闭** |
| **项目信息**              |                      |      |           |                 |                                                              |
| PROJECT_NAME              | 项目名称             | 是   | `String`  | 无              | 项目唯一标识，制作镜像、容器使用的名称，默认任务名称<br/>建议命名：项目名称-端类型[siweite-web] |
| PROJECT_TITLE             | 项目标题             | 是   | `String`  | 无              | 项目的中文名称，用于消息通知展示                             |
| PROJECT_URL               | 项目访问地址         | 否   | `String`  | 无              | 用于消息通知展示                                             |
| GIT_CODE_URL              | 代码仓库地址         | 是   | `String`  | 无              | 需要构建的代码仓库拉取地址                                   |
| **构建参数**              |                      |      |           |                 |                                                              |
| PIPELINE_RUN_NODE         | 构建运行的服务器节点 | 否   | `String`  | master          | 定义项目构建运行的 NODE服务器，TODO 目前不支持               |
| GIT_CODE_BRANCH           | 默认拉取分支         | 否   | `String`  | main            | 构建项目的代码仓库默认选择分支，在正式构建时还可手动选择     |
| CLEAN_CACHE_VALUE         | 清理缓存的默认方式   | 否   | `String`  | no_clean        | 清理缓存控制默认方式，可选：`no_clean` `clean_build` `clean_workspace` `clean_all`<br/>`no_clean`：不清理缓存和构建资源<br/>`clean_build`：清理构建资源，如：`node`清理`node_modules`<br/>`clean_workspace`:删除当前工作空间所有内容<br/>`clean_all`：清理包括`clean_build` `clean_workspace` |
| CLEAN_CACHE_PATH          | 清理路径             | 否   | `String`  | 无              | 默认清理当前项目`node_modules`，`node`构建并`CLEAN_CACHE_VALUE=clean_build或clean_all`生效<br/>相对于`node_modules`的相对路径，如：清理路径为`/.cache`，实际清理路径为当前项目的`node_modules/.cache` |
| GIT_CODE_SUB          | 子代码仓库列表         | 否  | `List<Map>` | 无                  | 子仓库信息，格式 `[['TARGET_DIR': 'view/siweite', 'URL': 'http://test.siweite.top/demo.git', 'BRANCH': 'main']]`<br/>`TARGET_DIR`：拉取的子项目仓库代码存放路径（相对于主项目路径）<br/>`URL`：子项目仓库地址<br/>`BRANCH`：拉取的仓库分支，为空时默认使用主项目分支 |
| **编译打包配置**          |                      |      |           |                 |                                                              |
| BUILD_NODE_TOOL           | Nodejs运行工具       | 是   | `String`  | 无              | Jenkins -> 系统管理 -> 全局工具配置 -> NodeJS安装 -> 别名    |
| BUILD_COMMAND             | 编译打包命令         | 是   | `String`  | `npm run build` | 多行命令使用 `\n` 分割 或 使用 `&&` 或使用 三引号            |
| TARGET_PATH               | 构建产物所在路径     | 是   | `String`  | 无              | 构建产物（打包输出）所在路径，支持目录、文件（相对于项目的路径）<br/>流水线会将产物移动到`Jenkinsfile`所在目录，方便制造镜像使用 |
| **部署配置**              |                      |      |           |                 |                                                              |
| DEPLOY_SERVER             | 部署服务器           | 是   | `List`    | 无              | 数组首个为默认值，值必须是：Jenkins -> 系统管理 -> 系统配置 -> SSH Servers -> Name |
| DEPLOY_ENV                | 部署环境             | 是   | `String`  | 无              | 如：`dev` `prod`写入环境变量，可以影响打包、启动读取的环境配置<br/>不配置，则不会添加环境变量 |
| DEPLOY_PORT               | 部署端口             | 否   | `String`  | 无              | 对外可访问端口，非docker内部端口，Docker对外映射端口<br/>可以不用配置，在`Jenkins`参数构建时输入<br/>当`RUN_NETWORK`=`bridge`，此参数必填 |
| RUN_PORT                  | 运行端口             | 否   | `String`  | 80              | 默认80端口，容器内部运行端口<br/>当`RUN_NETWORK`=`bridge`，此参数必填 |
| RUN_NETWORK               | 运行网络模式         | 否   | `String`  | `bridge`            | 默认`bridge`，容器运行网络模式，可选：`bridge`、`host`、`none`、`overlay`解释见：[网络驱动模式](https://docs.docker.com/engine/network/drivers/)<br/> 当等于`bridge`时，`DEPLOY_PORT`和`RUN_PORT`两参数必填 |
| RUN_ENV_KEY               | 运行环境键           | 否   | `String`  | 无                  | 容器运行环境变量的`key`，`value`= DEPLOY_ENV配置的值<br/>例如配置：RUN_ENV_KEY=SPRING_PROFILES_ACTIVE，就可以动态控制运行环境 |
| RUN_RESTART               | 重启策略             | 否   | `String`  | `no`                | 默认不自动重启，容器重启策略，可选：`no`、`on-failure[:max-retries]`、`always`、`unless-stopped`解释见：[自动启动容器](https://docs.docker.com/engine/containers/start-containers-automatically/) |
| **Harbor私服配置**        |                      |      |           |                 |                                                              |
| HARBOR_URL                | Harbor私服地址       | 是   | `String`  | 无              | Harbor私服地址，用于登录私服、推送镜像                       |
| HARBOR_PULL_LOGIN         | 拉取镜像是否需要登录 | 否   | `Boolean` | `false`         | 拉取部署到远程服务器上的镜像是否需要登录                     |
| HARBOR_PROJECT            | Harbor私服项目       | 是   | `String`  | 无              | Harbor私服项目，用于推送镜像到私服                           |
| BASE_IMAGE                | 使用的基础镜像       | 否   | `List`    | 无              | 提前拉取基础镜像，新版Docker（可能是Docker version 27.4.1以上，只测了这一个版本）的Dockerfile使用From做基础镜像必须是https，就需要提前拉取<br/>方式一：使用不强制https版本的Docker Engine<br/>方式二：填写此字段，流水线自动拉取填写的镜像（适合无法操作流水线服务器）[推荐]<br/>方式三：手动在流水线 制作镜像的服务器上拉取需要的镜像（一般来说是流水线服务器和制作镜像的服务器是同一个） |
| **凭证信息**              |                      |      |           |                 |                                                              |
| GIT_CODE_AUTH             | 代码仓库凭证         | 是   | `String`  | 无              | git代码仓库凭证，用于拉取待构建的代码仓库                    |
| HARBOR_AUTH               | 镜像仓库凭证         | 是   | `String`  | 无              | Harbor镜像仓库凭证，用于拉取、推送镜像                       |
| **配置选项配置**          |                      |      |           |                 |                                                              |
| BUILD_TIME_OUT            | 构建超时时间         | 否   | `String`  | 60              | 默认60分钟，针对于单个步骤的构建超时时间                     |
| DAYS_TO_KEEP_STR          | 构建保留天数         | 否   | `String`  | 无              | 丢弃旧的构建，构建保留此天数，为空保留天数不限制             |
| NUM_TO_KEEP_STR           | 构建保留个数         | 否   | `String`  | 15              | 默认保留15个，丢弃旧的构建，构建保留个数，为空保留个数不限制 |
| ARTIFACT_DAYS_TO_KEEP_STR | 构建产物保留天数     | 否   | `String`  | 无              | 丢弃旧的构建，构建产物保留天数，为空保留天数不限制           |
| ARTIFACT_NUM_TO_KEEP_STR  | 构建产物保留个数     | 否   | `String`  | 无              | 默认保留15个，丢弃旧的构建，构建产物保留个数，为空保留个数不限制 |
| **通知配置**              |                      |      |           |                 |                                                              |
| NOTIFY_TYPE               | 通知方式             | 否   | `String`  | 无              | 构建成功或失败发送通知消息，可选：`Feishu` `DingTalk` `QyWechat`，TODO目前只支持飞书<br/>当`STAGE_MESSAGE_NOTIFY`为`true`，此参数必填 |
| NOTIFY_WEBHOOK_URL        | 通知webhookUrl       | 否   | `String`  | 无              | 当`STAGE_MESSAGE_NOTIFY`为`true`，此参数必填                 |
| **节点控制参数配置**      |                      |      |           |                 |                                                              |
| STAGE_ARCHIVE_ARTIFACTS   | 是否开启构建产物     | 否   | `Boolean` | `false`         | 是否开启构建产物-步骤（开启后，支持下载打包后的产物）        |
| STAGE_MESSAGE_NOTIFY      | 是否开启通知消息     | 否   | `Boolean` | `false`         | 是否开启通知消息-步骤                                        |
