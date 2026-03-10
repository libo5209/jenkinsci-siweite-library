#!groovy

/**
 * 发送飞书通知
 *
 * @param noticeTitle 通知消息标题
 * @param projectUrl 项目访问地址
 * @param noticeLabel 通知标识
 * @param webhookUrl Webhook 地址
 * @param printLog 是否打印日志（调试使用）
 */
def call() {
    // 流水线是否执行成功
    def success = currentBuild.result == 'SUCCESS'
    def statusLabel = success ? "构建成功" : "构建失败"
    def statusColor = success ? "green" : "red"
    def statusIcon = success ? "✅" : "❌"
    def duration = currentBuild.getDurationString().replace(" and counting", "")
    def noticeButtonName = success && env.SWT_PROJECT_URL ? "跳转查看系统" : "查看构建日志"
    def noticeButtonType = success ? "primary" : "danger"
    def noticeButtonUrl = success && env.SWT_PROJECT_URL ? env.SWT_PROJECT_URL : env.BUILD_URL + "/console"

    if (env.SWT_NOTIFY_TYPE == "Feishu") {
        lark(
                robot: "${env.SWT_NOTIFY_ROBOT_ID}",
                type: 'CARD',
                title: "📢 ${statusIcon}${statusLabel}，智能通知系统部署：",
                text: [
                        "📋 **项目名称**: ${env.SWT_PROJECT_TITLE}",
                        "🏷️ **项目标识**: ${env.SWT_PROJECT_NAME}",
                        "🌟 **构建状态**: <text_tag color='${statusColor}'>${statusLabel}</text_tag>",
                        "🕐 **构建耗时**: ${duration}",
                        "👤 **构建人员**: ${env.BUILD_USER}",
                        '<at user_id="all">所有人</at>'
                ],
                buttons: [
                        [
                                title: "${noticeButtonName}",
                                type: "${noticeButtonType}",
                                url: "${noticeButtonUrl}"
                        ]
                ]
        )
    } else if (env.SWT_NOTIFY_TYPE == "DingTalk") {
        dingTalk(
                robot: "${env.SWT_NOTIFY_ROBOT_ID}",
                type: 'CARD',
                title: "📢 ${statusIcon}${statusLabel}，智能通知系统部署：",
                text: [
                        "### <font color='${statusColor}'>📢 ${statusIcon}${statusLabel}，智能通知系统部署：</font> \n",
                        '--- \n',
                        "📋 **项目名称**: ${env.SWT_PROJECT_TITLE} \n",
                        "🏷️ **项目标识**: ${env.SWT_PROJECT_NAME} \n",
                        "🌟 **构建状态**: <font color='${statusColor}'>${statusLabel}</font> \n",
                        "🕐 **构建耗时**: ${duration} \n",
                        "👤 **构建人员**: ${env.BUILD_USER} \n"
                ],
                atAll: true,
                buttons: [
                        [
                                title: "${noticeButtonName}",
                                type: "${noticeButtonType}",
                                url: "${noticeButtonUrl}"
                        ]
                ]
        )
    } else {
        println "未知消息通知类型：${env.SWT_NOTIFY_TYPE}，目前只支持 [Feishu、DingTalk]"
    }
}
