import hudson.FilePath
import hudson.model.ParametersAction
import hudson.model.FileParameterValue

/**
 * 保存构建参数 name 的临时文件
 *
 * @param name 参数名称
 * @param fname 指定保存文件名称，为空默认原始文件名称
 *
 * @return 临时保存文件地址
 */
def call(String name, String fname = null) {
    def paramsAction = currentBuild.rawBuild.getAction(ParametersAction.class);
    if (paramsAction != null) {
        for (param in paramsAction.getParameters()) {
            if (param.getName().equals(name)) {
                if (! (param instanceof FileParameterValue)) {
                    error "unstashParam: not a file parameter: ${name}"
                }
                if (!param.getOriginalFileName()) {
                    error "unstashParam: file was not uploaded"
                }
                if (env['NODE_NAME'] == null) {
                    error "unstashParam: no node in current context"
                }
                if (env['WORKSPACE'] == null) {
                    error "unstashParam: no workspace in current context"
                }

                if (env['NODE_NAME'].equals("master") || env['NODE_NAME'].equals("built-in")) {
                    workspace = new FilePath(null, env['WORKSPACE'])
                } else {
                    workspace = new FilePath(Jenkins.getInstance().getComputer(env['NODE_NAME']).getChannel(), env['WORKSPACE'])
                }

                filename = fname == null ? param.getOriginalFileName() : fname
                file = workspace.child(filename)

                destFolder = file.getParent()
                destFolder.mkdirs()

                file.copyFrom(param.getFile())
                return filename;
            }
        }
    }
    error "unstashParam: No file parameter named '${name}'"
}