package com.yupi.zojcodesandbox;

import com.yupi.zojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.zojcodesandbox.model.ExecuteCodeResponse;

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
