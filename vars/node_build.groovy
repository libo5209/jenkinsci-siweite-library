#!groovy

import top.siweite.plugins.model.BuildArgsModel

/**
 * Node编译打包模板 流水线
 *
 * @param buildArgs 流水线参数
 */
def call(BuildArgsModel buildArgs) {
    // 定义全局的Map来存储计算变量
    def globalVars = [:]

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
        }
        post {
            always {
                withBuildUser {
                    script {
                        // 更改构建名称
                        buildName "#${env.BUILD_ID}-${params.DEPLOY_BRANCH}-${env.BUILD_USER}"
                        // 更改构建描述
                        buildDescription("""提交用户: ${globalVars['CODE_COMMIT_USER']}
                            |提交SHA: ${globalVars['CODE_COMMIT_SHA']}
                            |提交时间: ${globalVars['CODE_COMMIT_TIME']}
                            |提交内容: ${globalVars['CODE_COMMIT_INFO']}""".stripMargin())
                        // 发送通知
                        if (buildArgs.stageMessageNotify) {
                            send_notifications()
                        }
                        // 输出构建参数
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
