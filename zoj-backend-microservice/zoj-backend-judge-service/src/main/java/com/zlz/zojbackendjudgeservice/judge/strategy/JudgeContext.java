package com.zlz.zojbackendjudgeservice.judge.strategy;


import com.zlz.zojbackendmodel.codesandbox.JudgeInfo;
import com.zlz.zojbackendmodel.dto.question.JudgeCase;
import com.zlz.zojbackendmodel.entity.Question;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
