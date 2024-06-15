package com.zlz.zojbackendjudgeservice.judge.codesandbox.impl;


import com.zlz.zojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.zlz.zojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.zlz.zojbackendmodel.codesandbox.JudgeInfo;
import com.zlz.zojbackendmodel.enums.JudgeInfoMessageEnum;
import com.zlz.zojbackendmodel.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 实例代码沙箱（仅为了跑通流程）
 */
@Slf4j
public class ExampleCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
