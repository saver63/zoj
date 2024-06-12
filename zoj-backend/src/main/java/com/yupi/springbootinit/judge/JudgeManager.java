package com.yupi.springbootinit.judge;

import com.yupi.springbootinit.judge.strategy.DefaultJudgeStrategy;
import com.yupi.springbootinit.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.springbootinit.judge.strategy.JudgeContext;
import com.yupi.springbootinit.judge.strategy.JudgeStrategy;
import com.yupi.springbootinit.judge.codesandbox.model.JudgeInfo;
import com.yupi.springbootinit.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）:做了一层封装
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        //如果是java程序
        if ("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }

        return judgeStrategy.doJudge(judgeContext);
    }
}
