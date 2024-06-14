package com.yupi.zojcodesandbox;

import com.yupi.zojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.zojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;


/**
 * Java 原始代码沙箱实现（直接复用模板方法）
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {

    //可以自定义流程
//    @Override
//    public File saveCodeFile(String code) {
//        File file = super.saveCodeFile(code);
//        System.out.println("监控");
//        return file;
//    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
