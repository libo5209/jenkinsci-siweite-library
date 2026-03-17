<h1 align="center" style="text-align:center;">
  jenkinsci-siweite-library
</h1>
<p align="center">
    <strong>让 CI/CD 变得简单，让您更专注于创造价值！</strong>
    <br/>
    <strong>简单、快速、易上手</strong>
</p>
<p align="center">
    👉<a>http://jenkins.siweite.top</a>👈
</p>
<p align="center"> 
    如果这个项目对您有帮助，感谢点个Star⭐支持一下<br/>
    <a target="_blank" href="https://gitee.com/siweite/jenkinsci-siweite-library">
        <img src="https://gitee.com/siweite/jenkinsci-siweite-library/badge/star.svg?theme=dark" alt="Gitee Stars">
    </a>
    <a target="_blank" href='https://github.com/libo5209/jenkinsci-siweite-library'>
        <img src="https://img.shields.io/github/stars/libo5209/jenkinsci-siweite-library?style=flat&logo=github" alt="github star"/>
    </a>
</p>

**语言：** 中文

---

不懂`Jenkins pipeline`语法也能轻松出搭建企业级流水线

---

## 一 项目介绍

### 1.1 🎉项目背景

"最初只是为了减少重复工作（顺便摸鱼🐟🐟），现在希望能够帮助更多小伙伴从繁琐的流水线配置中解放出来。" —— `siweite`

### 1.2 🌟项目概述

`jenkinsci-siweite-library`是一个基于`Jenkins`共享库技术构建的企业级流水线模板库，旨在通过简单的配置快速搭建标准化、可复用的`CI/CD`流水线。无论您是`Jenkins`新手还是经验丰富的`DevOps`工程师，都能在几分钟内完成原本需要数小时甚至数天的流水线配置工作。

### 1.3 🎯项目特色

#### 1.3.1 解决的核心痛点

- 重复劳动：为每个项目单独编写和维护流水线脚本
- 学习曲线：Jenkins Pipeline 语法复杂，上手难度高
- 标准不一：团队内流水线实现方式不一致
- 维护困难：多个项目中相似的流水线逻辑需要同步更新

#### 1.3.2 带来的核心价值

- 🚀 快速部署：几行配置即可完成企业级流水线搭建
- 📚 零学习成本：无需掌握复杂的 Jenkins Pipeline 语法
- 🔧 标准化：统一团队的 CI/CD 实践和流程规范
- 🔄 可复用：一次封装，多处使用，便于维护和升级

## 二 使用指南

### 2.1 支持模板

| 标题            | 模板使用方法                              | 阶段/步骤说明(`[]`：可选、`<>`：必选、`{}`：选项必选)                                 |
|:--------------|:------------------------------------|:-------------------------------------------------------------------|
| Java 发布私仓     | `SiweiteCI.javaPublishMaven(args)`  | [清理缓存]→[仓库合并]→<项目编译>→<制作产物>→[发送通知]                                 |
| Java Docker部署 | `SiweiteCI.javaDeployDocker(args)`  | [清理缓存]→[仓库合并]→<项目编译>→<覆盖配置>→[制作产物]→<制作镜像>→{推送私服\|传输镜像}→部署容器→[发送通知] |
| Node Docker部署 | `SiweiteCI.nodeDeployDocker(args)`  | [清理缓存]→[仓库合并]→<项目编译>→<覆盖配置>→[制作产物]→<制作镜像>→{推送私服\|传输镜像}→部署容器→[发送通知] |


### 2.2 流水线示例

- Java发布私仓流水线配置

```groovy
@Library('jenkinsci-siweite-library@0.4.0') _

// 控制参数
def args = [
    // 项目名称
    projectName: 'siweite-common',
    // 项目标题
    projectTitle: 'siweite快速开发平台[基础依赖模块]',
    // 代码仓库地址
    gitCodeUrl: 'http://192.168.2.110:3000/siweite/siweite-common.git',

    // 代码仓库默认选择分支
    gitCodeBranch: 'master',

    // jdk打包工具，Jenkins -> 系统管理 -> 全局工具配置 -> JDK安装 -> 别名
    buildJdkTool: 'jdk21',
    // maven编译工具，Jenkins -> 系统管理 -> 全局工具配置 -> Maven安装 -> 别名
    buildMavenTool: 'M399',
    // 编译打包命令
    buildCommand: '''
        mvn -Dmaven.test.skip=true clean deploy -T 1C
    ''',

    // 代码仓库凭证
    gitCodeAuth: '054e6886-1aec-4b81-b28b-fb033d942153',
]
SiweiteCI.javaPublishMaven(args)
```

- SpringBoot项目流水线配置

```groovy
@Library('jenkinsci-siweite-library@0.4.0') _

// 控制参数
def args = [
    // 部署项目名称
    projectName: 'siweite-java-boot',
    // 项目标题
    projectTitle: 'siweite快速开发平台[服务端]',
    // git主代码仓库地址，用于部署的代码仓库地址
    gitCodeUrl: 'http://192.168.2.110:3000/siweite/siweite-java-boot.git',

    // git主代码仓库默认选择分支
    gitCodeBranch: 'master',

    // jdk打包工具，Jenkins -> 系统管理 -> 全局工具配置 -> JDK安装 -> 别名
    buildJdkTool: 'jdk21',
    // maven编译工具，Jenkins -> 系统管理 -> 全局工具配置 -> Maven安装 -> 别名
    buildMavenTool: 'M399',
    // 编译打包命令，多行命令使用 \n 分割 或 使用 && 或者使用 多行
    buildCommand: '''
        mvn -Dmaven.test.skip=true clean package -T 1C
    ''',
    // 构建产物（打包输出）所在路径，支持目录、文件（相对于项目的路径）
    targetPath: 'siweite-admin/target/*.jar',

    /*
     * 部署配置
     */
    // 部署服务器，Jenkins -> 系统管理 -> 系统配置 -> SSH Servers -> Name
    deployServer: ['server1', 'server2'],
    // 部署环境，写入环境变量，影响打包、启动读取的环境配置
    deployEnv: 'prod',
    // 部署端口，对外可访问端口，非docker内部端口
    deployPort: 30000,
    // 运行端口，容器内部访问端口，程序启动占用端口
    runPort: 30000,

    // git代码仓库凭证，用于拉取待构建的代码仓库
    gitCodeAuth: '054e6886-1aec-4b81-b28b-fb033d942153',
]

SiweiteCI.javaDeployDocker(args)
```

- Solon项目流水线配置

```groovy
@Library('jenkinsci-siweite-library@0.4.0') _

// 控制参数
def args = [
    // 项目名称
    projectName: 'siweite-java-boot',
    // 项目标题
    projectTitle: 'siweite快速开发平台[服务端]',
    // git主代码仓库地址，用于部署的代码仓库地址
    gitCodeUrl: 'http://192.168.2.110:3000/siweite/siweite-java-boot.git',

    // git主代码仓库默认选择分支
    gitCodeBranch: 'master',

    // jdk打包工具，Jenkins -> 系统管理 -> 全局工具配置 -> JDK安装 -> 别名
    buildJdkTool: 'jdk21',
    // maven编译工具，Jenkins -> 系统管理 -> 全局工具配置 -> Maven安装 -> 别名
    buildMavenTool: 'M399',
    // 编译打包命令，多行命令使用 \n 分割 或 使用 && 或者使用 多行
    buildCommand: '''
        mvn -Dmaven.test.skip=true clean package -T 1C
    ''',
    // 构建产物（打包输出）所在路径，支持目录、文件（相对于项目的路径）
    targetPath: 'siweite-admin/target/*.jar',

    /*
     * 部署配置
     */
    // 部署服务器，Jenkins -> 系统管理 -> 系统配置 -> SSH Servers -> Name
    deployServer: ['server1', 'server2'],
    // 部署环境，写入环境变量，影响打包、启动读取的环境配置
    deployEnv: 'prod',
    // 部署端口，对外可访问端口，非docker内部端口
    deployPort: 30000,
    // 运行端口，容器内部访问端口，程序启动占用端口
    runPort: 30000,
    // 运行环境变量key，value= DEPLOY_ENV配置的值
    runEnvKey: 'solon.env',

    // git代码仓库凭证，用于拉取待构建的代码仓库
    gitCodeAuth: '054e6886-1aec-4b81-b28b-fb033d942153',
]

SiweiteCI.javaDeployDocker(args)
```

- Node项目流水线配置

```groovy
@Library('jenkinsci-siweite-library@0.4.0') _

// 控制参数
def args = [
    // 项目名称
    projectName: 'siweite-web',
    // 项目标题
    projectTitle: 'siweite快速开发平台[web端]',
    // git主代码仓库地址，用于部署的代码仓库地址
    gitCodeUrl: 'http://192.168.2.110:3000/siweite/siweite-web.git',

    // git主代码仓库默认选择分支
    gitCodeBranch: 'master',

    // NODE编译工具，Jenkins -> 系统管理 -> 全局工具配置 -> NodeJS安装 -> 别名
    buildNodeTool: 'node20',
    // 编译打包命令，多行命令使用 \n 分割 或 使用 && 或者使用 多行
    buildCommand: '''
        pnpm config set registry http://192.168.2.110:8081/repository/npm-group/
        pnpm install
        pnpm build
    ''',
    // 构建产物（打包输出）所在路径，支持目录、文件（相对于项目的路径）
    targetPath: 'dist',

    /*
     * 部署配置
     */
    // 部署服务器，Jenkins -> 系统管理 -> 系统配置 -> SSH Servers -> Name
    deployServer: ['server1', 'server2'],
    // 部署环境，写入环境变量，影响打包、启动读取的环境配置
    deployEnv: 'prod',
    // 部署端口，对外可访问端口，非docker内部端口
    deployPort: 8000,

    // git代码仓库凭证，用于拉取待构建的代码仓库
    gitCodeAuth: '054e6886-1aec-4b81-b28b-fb033d942153',

]

SiweiteCI.nodeDeployDocker(args)
```

### 2.3 快速开始

[📘使用文档](./docs/start.md)
[📙示例项目](https://gitee.com/siweite/siweite-sample-pipeline)

## 三 后续计划

- [x] 支持`Java Maven`发布私仓
- [ ] 支持`Java Maven`远程推送`jar`，命令启动部署
- [x] 支持`Java Maven`远程推送`Registry`服务器（例如：`Harbor`），使用私仓`Dokcer`容器部署
- [x] 支持`Node`远程推送`Registry`服务器（例如：`Harbor`）私仓，使用`Dokcer`容器部署
- [ ] 支持`Node`远程推送`dist`，命令启动部署
- [x] 远程`Docker`容器部署，支持不用推送私仓
- [x] 支持项目环境配置开发运维分离
- [x] 支持自定义`Docker`启动参数（如：映射端口、网络模式、自启策略、环境变量等）
- [x] 支持多个子代码仓库整合到主代码仓库进行构建
- [x] 自定义配置开启流水线构建产物（Maven：jar包、Node: dist）
- [x] 自定义配置开启发送构建通知消息（支持钉钉、飞书、~~企业微信~~）
- [ ] 支持自定义选择版本进行远程回滚部署
- [ ] 支持自定义构建容器镜像`tag`
- [ ] 支持指定`Jenkins`运行任务节点

### 🤝 贡献与支持

如果项目对您有一点帮助，请不要吝啬一个⭐！
- ⭐gitee: https://gitee.com/siweite/jenkinsci-siweite-library
- ⭐github: https://github.com/libo5209/jenkinsci-siweite-library

### 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](./LICENSE) 文件。
