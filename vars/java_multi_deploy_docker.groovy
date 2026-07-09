#!groovy

import top.siweite.plugins.model.BuildArgsModel

/**
 * Java远程Docker部署模板 流水线方法
 *
 * @param buildArgs 流水线参数
 */
def call(BuildArgsModel buildArgs) {
    // 定义全局的Map来存储计算变量
    def globalVars = [:]

    // 提取所有部署项目列表
    def deployProjects = buildArgs.multiProjectMap.keySet() as List
    deployProjects.add(0, 'all')

    // 声明式流水线风格--整个构建流程
    pipeline {
        // 流水线执行位置
        agent { label buildArgs.runNode }

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
            // 部署项目
            choice name: 'DEPLOY_PROJECTS', choices: deployProjects, description: '请选择需要部署的项目'
            // 构建模式：发布 或 回滚
            choice(name: 'DEPLOY_MODE', choices: ['DEPLOY','ROLLBACK'], description: '请选择发布或者回滚？')
        }

        // 安装配置工具
        tools {
            // 安装配置为“buildArgs.buildMavenTool”的maven版本并将其添加到路径中
            maven buildArgs.buildMavenTool
            // 安装配置为“buildArgs.buildJdkTool”的Jdk版本并将其添加到路径中
            jdk buildArgs.buildJdkTool
        }

        stages {
            stage('清理缓存') {
                steps {
                    script {
                        // 计算需要部署的项目,以数组展示
                        globalVars['DEPLOY_PROJECTS'] = "${params.DEPLOY_PROJECTS}".split(',')
                        def isAll = globalVars['DEPLOY_PROJECTS'].contains("all");
                        if (isAll) {
                            globalVars['DEPLOY_PROJECTS'] = deployProjects
                            globalVars['DEPLOY_PROJECTS'].remove("all")
                        }

                        // 校验必须存在一个及以上待部署项目
                        if (!globalVars['DEPLOY_PROJECTS'] || globalVars['DEPLOY_PROJECTS'].size() == 0) {
                            error '部署项目 [DEPLOY_PROJECTS] 构建参数必选'
                        }
                        if ('clean_build' == params.CLEAN_CACHE || 'clean_all' == params.CLEAN_CACHE) {
                            def mavenVersion = sh(script: "mvn -v | grep 'Apache Maven' | awk '{print \$3}'", returnStdout: true).trim()
                            globalVars['MAVEN_CLEAN_COMMAND'] = getCleanMavenCommand(version: mavenVersion, cleanCachePath: buildArgs.cleanCachePath)
                            println "清理缓存-清理本地仓库"
                            sh "${globalVars['MAVEN_CLEAN_COMMAND']}"

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
                    // 对所有需要部署的项目制作产物
                    script {
                        globalVars['DEPLOY_PROJECTS'].each { project ->
                            archiveArtifacts artifacts: "${buildArgs.projectName}/${buildArgs.multiProjectMap[project].targetPath}", fingerprint: true, followSymlinks: false, onlyIfSuccessful: true
                        }
                    }
                }
            }
            stage('制作镜像') {
                steps {
                    script {
                        // 登录Registry服务器 (用于拉取镜像或推送镜像到Registry服务器)
                        if (buildArgs.imagePullLogin || buildArgs.imagePushRegistry) {
                            withCredentials([usernamePassword(credentialsId: "${buildArgs.registryAuth}", passwordVariable: 'password', usernameVariable: 'username')]) {
                                sh "docker login -u ${username} -p ${password} ${buildArgs.registryUrl}"
                            }
                        }
                        try {
                            // 提前拉取无法在构建的时候拉取的镜像
                            buildArgs.baseImage.each { image ->
                                sh "docker pull ${image}"
                            }
                            // 构建所有部署项目的 docker镜像
                            globalVars['DEPLOY_PROJECTS'].each { project ->
                                env.SWT_TMP_PROJECT = "${project}"
                                def buildDockerImageCommand = getBuildDockerImageCommand(codeProjectTag: globalVars['CODE_PROJECT_TAG'], projectName: "${project}")
                                sh "${buildDockerImageCommand}"
                            }
                        } catch (Exception e) {
                            error e.message
                        }

                    }
                }
                post {
                    always {
                        script {
                            // 删除所有部署项目打包产物
                            globalVars['DEPLOY_PROJECTS'].each { project ->
                                sh "rm -rf ./${buildArgs.projectName}/${buildArgs.multiProjectMap[project].targetPath}"
                            }
                            // 退出登录Registry服务器
                            if (buildArgs.imagePullLogin || buildArgs.imagePushRegistry) {
                                sh "docker logout ${buildArgs.registryUrl}"
                            }
                        }
                    }
                }
            }
            stage('部署应用') {
                steps {
                    script {
                        println "开始部署"
                        def remoteDirectory = buildArgs.imagePushRegistry ? '' : buildArgs.imageTempSavePath
                        globalVars['SOURCE_FILES'] = []
                        // 对所有部署项目进行远程部署
                        globalVars['DEPLOY_PROJECTS'].each { project ->
                            try {
                                globalVars['DEPLOY_COMMAND'] = getDeployDockerCommand(codeProjectTag: globalVars['CODE_PROJECT_TAG'], projectName: project)
                            } catch (Exception e) {
                                error e.message
                            }
                            // 获取项目镜像名称
                            def projectImageName = env["SWT_${project.toUpperCase()}_IMAGE_NAME"]
                            def sourceFiles = buildArgs.imagePushRegistry ? '' : "${projectImageName}-${globalVars['CODE_PROJECT_TAG']}.tar"

                            globalVars['SOURCE_FILES'].add(sourceFiles)

                            // 向部署服务器发送部署指令
                            if (buildArgs.imagePullLogin) {
                                withCredentials([usernamePassword(credentialsId: "${buildArgs.registryAuth}", passwordVariable: 'password', usernameVariable: 'username')]) {
                                    sshPublisher(publishers: [sshPublisherDesc(configName: "${params.DEPLOY_SERVER}", transfers: [sshTransfer(cleanRemote: false, excludes: '',
                                            execCommand: """
                                            docker login -u ${username} -p ${password} ${buildArgs.registryUrl}
                                            ${globalVars['DEPLOY_COMMAND']}
                                            docker logout ${buildArgs.registryUrl}
                                        """,
                                            execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: "${remoteDirectory}",
                                            remoteDirectorySDF: false, removePrefix: '', sourceFiles: "${sourceFiles}")], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: buildArgs.debug)])
                                }
                            } else {
                                sshPublisher(publishers: [sshPublisherDesc(configName: "${params.DEPLOY_SERVER}", transfers: [sshTransfer(cleanRemote: false, excludes: '',
                                        execCommand: "${globalVars['DEPLOY_COMMAND']}",
                                        execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: "${remoteDirectory}",
                                        remoteDirectorySDF: false, removePrefix: '', sourceFiles: "${sourceFiles}")], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: buildArgs.debug)])
                            }
                        }
                    }
                }
                post {
                    always {
                        // 删除保存的镜像包
                        script {
                            if (!buildArgs.imagePushRegistry) {
                                sh " rm -rf ${globalVars['SOURCE_FILES'].join(' ')}"
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
