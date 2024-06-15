package com.zlz.zojbackendjudgeservice.judge.codesandbox;


import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱的接口定义
 */
public interface CodeSandbox {


    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
