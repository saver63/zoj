package com.zlz.zojbackendjudgeservice.judge.codesandbox.impl;


import com.zlz.zojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
