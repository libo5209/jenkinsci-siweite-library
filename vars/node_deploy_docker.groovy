#!groovy

import top.siweite.plugins.model.BuildArgsModel

/**
 * Node远程Docker部署模板 流水线方法
 *
 * @param buildArgs 流水线参数
 */
def call(BuildArgsModel buildArgs) {
    // 定义全局的Map来存储计算变量
    def globalVars = [:]
    // 预处理参数的类型
    def deployPortStr = buildArgs.deployPort.toString()

    // 声明式流水线风格--整个构建流程
    pipeline {
        // 流水线执行位置
        agent any

        // 配置选项
        options {
            // 关闭自动检出代码
            skipDefaultCheckout true
            // 不允许并发构建
            disableConcurrentBuilds()
            // 构建超时时间
            timeout(buildArgs.buildTimeOut)
            // 丢弃旧的构建
            buildDiscarder logRotator(artifactDaysToKeepStr: buildArgs.artifactDaysToKeepStr, artifactNumToKeepStr: buildArgs.artifactNumToKeepStr, daysToKeepStr: buildArgs.daysToKeepStr, numToKeepStr: buildArgs.numToKeepStr)
        }

        // 构建参数
        parameters {
            // 清理缓存
            choice name: 'CLEAN_CACHE', choices: buildArgs.cleanCacheOptions, description: '请选择清理资源方式'
            // 项目分支参数
            string name: 'DEPLOY_BRANCH', defaultValue: buildArgs.gitCodeBranch, description: '选择需要部署项目的分支'
            // 部署服务器
            choice name: 'DEPLOY_SERVER', choices: buildArgs.deployServer, description: '选择需要部署服务器'
            // 运行端口
            string name: 'DEPLOY_PORT', defaultValue: deployPortStr, trim: true, description: '选择需要运行端口'
            // 构建模式：发布 或 回滚
            choice(name: 'DEPLOY_MODE', choices: ['DEPLOY','ROLLBACK'], description: '请选择发布或者回滚？')
        }

        // 安装配置工具
        tools {
            // 安装配置为“buildArgs.buildNodeTool”的nodejs版本并将其添加到路径中
            nodejs buildArgs.buildNodeTool
        }

        stages {
            stage('清理缓存') {
                steps {
                    script {
                        if ('clean_build' == params.CLEAN_CACHE) {
                            println "清理缓存-清理本地依赖"
                            sh "rm -rf ./${buildArgs.projectName}/node_modules/${buildArgs.cleanCachePath}"
                        }
                        if ('clean_workspace' == params.CLEAN_CACHE || 'clean_all' == params.CLEAN_CACHE) {

                            println "清理缓存-清理工作空间"
                            deleteDir()
                        }
                    }
                }
            }
            stage('拉取代码') {
                steps {
                    checkBuildParams()
                    // 拉取流水线资源
                    checkout changelog: buildArgs.debug, poll: buildArgs.debug, scm: scmGit(branches: scm.branches,
                            doGenerateSubmoduleConfigurations: false,
                            extensions: scm.extensions + [cloneOption(depth: 1, shallow: true)],
                            userRemoteConfigs: scm.userRemoteConfigs)
                    // 拉取待部署项目
                    dir(buildArgs.projectName) {
                        checkout changelog: buildArgs.debug, poll: buildArgs.debug, scm: scmGit(branches: [[name: params.DEPLOY_BRANCH]],
                                doGenerateSubmoduleConfigurations: false,
                                extensions: [],
                                userRemoteConfigs: [[url: buildArgs.gitCodeUrl, credentialsId: buildArgs.gitCodeAuth]])
                        script {
                            try {
                                /*
                                 * 定义全局变量
                                 */
                                // 提交SHA（Git仓库提交SHA）
                                globalVars['CODE_COMMIT_SHA'] = sh(script: 'git log -1 --pretty=format:%h', returnStdout: true).trim()
                                // 提交用户
                                globalVars['CODE_COMMIT_USER'] = sh(script: 'git log -1 --pretty=format:%an', returnStdout: true).trim()
                                // 提交时间
                                globalVars['CODE_COMMIT_TIME'] = sh(script: 'git log -1 --pretty=format:%ai', returnStdout: true).trim()
                                // 提交信息
                                globalVars['CODE_COMMIT_INFO'] = sh(script: 'git log -1 --pretty=format:%s', returnStdout: true).trim()
                                // 对应构建的版本 时间+CODE_COMMIT_SHA+buildID
                                globalVars['CODE_PROJECT_TAG'] = sh(script: "date '+%Y%m%d%H%M%S'" + "-${globalVars['CODE_COMMIT_SHA']}" + "-${env.BUILD_ID}", returnStdout: true).trim()
                            } catch (Exception e) {
                                error e.message
                            }
                        }
                        script {
                            // 拉取子项目整合到主项目中
                            buildArgs.gitCodeSub.each { repo ->
                                // 创建拉取仓库的目标文件夹
                                sh "mkdir -p ${repo.targetDir}"
                                dir(repo.targetDir) {
                                    checkout changelog: buildArgs.debug, poll: buildArgs.debug, scm: scmGit(branches: [[name: repo.branch ?: params.DEPLOY_BRANCH]],
                                            doGenerateSubmoduleConfigurations: false,
                                            extensions: [cloneOption(depth: 1, shallow: true)],
                                            userRemoteConfigs: [[url: repo.url, credentialsId: buildArgs.gitCodeAuth]])
                                }
                            }
                        }
                    }
                }
            }
            stage('项目编译') {
                steps {
                    // 复制资源文件进行 覆盖 项目中的文件
                    sh """
                        if [ -d './${JOB_NAME}/resource' ]; then
                            cp -af ./${JOB_NAME}/resource/. ./${buildArgs.projectName}/
                        fi
                    """
                    dir(buildArgs.projectName) {
                        sh """
                            ${buildArgs.buildCommand}
                        """
                    }
                }
            }
            stage('制作产物') {
                when {
                    expression { buildArgs.stageArchiveArtifacts }
                }
                steps {
                    dir (buildArgs.projectName) {
                        sh "tar -zcvf dist.tar.gz ${buildArgs.targetPath}"
                    }
                    archiveArtifacts artifacts: "${buildArgs.projectName}/dist.tar.gz", fingerprint: true, followSymlinks: false, onlyIfSuccessful: true
                }
            }
            stage('制作镜像') {
                steps {
                    // 登录Registry服务器 (用于推送镜像到Registry服务器)
                    script {
                        if (buildArgs.imagePullLogin) {
                            withCredentials([usernamePassword(credentialsId: "${buildArgs.registryAuth}", passwordVariable: 'password', usernameVariable: 'username')]) {
                                sh "docker login -u ${username} -p ${password} ${buildArgs.registryUrl}"
                            }
                        }

                        // 提前拉取无法在构建的时候拉取的镜像
                        buildArgs.baseImage.each { image ->
                            sh "docker pull ${image}"
                        }
                        if (!buildArgs.imagePushRegistry && buildArgs.imagePullLogin) {
                            sh "docker logout ${buildArgs.registryUrl}"
                        }
                        try {
                            if (buildArgs.imagePushRegistry) {
                                withCredentials([usernamePassword(credentialsId: "${buildArgs.registryAuth}", passwordVariable: 'password', usernameVariable: 'username')]) {
                                    sh "docker login -u ${username} -p ${password} ${buildArgs.registryUrl}"
                                }
                            }
                            // 构建docker镜像
                            def buildDockerImageCommand = getBuildDockerImageCommand(codeProjectTag: globalVars['CODE_PROJECT_TAG'])
                            sh "${buildDockerImageCommand}"
                        } catch (Exception e) {
                            error e.message
                        } finally {
                            if (buildArgs.imagePushRegistry) {
                                sh "docker logout ${buildArgs.registryUrl}"
                            }
                        }

                    }
                }
                post {
                    always {
                        // 删除打包产物
                        sh """
                            rm -rf ./${env.JOB_NAME}/${buildArgs.targetPath}
                        """
                    }
                }
            }
            stage('部署应用') {
                steps {
                    script {
                        if (buildArgs.imagePullLogin) {
                            withCredentials([usernamePassword(credentialsId: "${buildArgs.registryAuth}", passwordVariable: 'password', usernameVariable: 'username')]) {
                                try {
                                    globalVars['deployCommand'] = getDeployDockerCommand(
                                            codeProjectTag: globalVars['CODE_PROJECT_TAG'],
                                            publishPort: params.DEPLOY_PORT,
                                            repoUsername: username,
                                            repoPassword: password
                                    )
                                } catch (Exception e) {
                                    error e.message
                                }
                            }
                        } else {
                            try {
                                globalVars['deployCommand'] = getDeployDockerCommand(
                                        codeProjectTag: globalVars['CODE_PROJECT_TAG'],
                                        publishPort: params.DEPLOY_PORT
                                )
                            } catch (Exception e) {
                                error e.message
                            }
                        }
                    }
                    script {
                        def remoteDirectory = buildArgs.imagePushRegistry ? '' : buildArgs.imageTempSavePath
                        def sourceFiles = buildArgs.imagePushRegistry ? '' : "${env.SWT_IMAGE_NAME}-${globalVars['CODE_PROJECT_TAG']}.tar"
                        try {
                            // 向部署服务器发送部署指令
                            sshPublisher(publishers: [sshPublisherDesc(configName: "${params.DEPLOY_SERVER}", transfers: [sshTransfer(cleanRemote: false, excludes: '',
                                    execCommand: "${globalVars['deployCommand']}",
                                    execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: "${remoteDirectory}",
                                    remoteDirectorySDF: false, removePrefix: '', sourceFiles: "${sourceFiles}")], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: buildArgs.debug)])
                        } finally {
                            // 删除保存的镜像包
                            if (!buildArgs.imagePushRegistry) {
                                sh " rm -rf ${sourceFiles}"

                            }
                        }
                    }
                }
            }
        }
        post {
            always {
                withBuildUser {
                    script {
                        if (params.DEPLOY_MODE == "DEPLOY") {
                            buildName "#${env.BUILD_ID}-${params.DEPLOY_BRANCH}-${env.BUILD_USER}"
                            buildDescription("""提交用户: ${globalVars['CODE_COMMIT_USER']}
                                |运行服务器: ${params.DEPLOY_SERVER}
                                |提交SHA: ${globalVars['CODE_COMMIT_SHA']}
                                |提交时间: ${globalVars['CODE_COMMIT_TIME']}
                                |提交内容: ${globalVars['CODE_COMMIT_INFO']}""".stripMargin())
                        } else {
                            buildName "#${env.BUILD_ID}-${params.DEPLOY_BRANCH}-${env.BUILD_USER}"
                            buildDescription "回滚版本号为: ${params.ROLLBACK_VERSION}"
                        }
                        if (buildArgs.stageMessageNotify) {
                            send_notifications()
                        }
                        if (buildArgs.debug) {
                            sh 'printenv'
                            println "${buildArgs}"
                        }
                    }
                }
            }
        }
    }

}
