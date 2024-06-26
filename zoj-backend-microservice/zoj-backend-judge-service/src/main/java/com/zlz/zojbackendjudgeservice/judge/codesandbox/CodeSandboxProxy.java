package com.zlz.zojbackendjudgeservice.judge.codesandbox;


import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类：增强代码沙箱的能力（增加打印输出）
 * 本质上就相当于是中介
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息"+executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息"+executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
