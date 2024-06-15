package com.zlz.zojbackendjudgeservice.judge.codesandbox;


import com.zlz.zojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandboxImpl;
import com.zlz.zojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandboxImpl;
import com.zlz.zojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandboxImpl;

/**
 * 代码沙箱工厂(根据字符串参数创建指定的代码沙箱实例)
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     * @param type 沙箱类型
     * @return
     */
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandboxImpl();
            case "remote":
                return new RemoteCodeSandboxImpl();
            case "thirdParty":
                return new ThirdPartyCodeSandboxImpl();
            default:
                return new ExampleCodeSandboxImpl();
        }
    }
}
