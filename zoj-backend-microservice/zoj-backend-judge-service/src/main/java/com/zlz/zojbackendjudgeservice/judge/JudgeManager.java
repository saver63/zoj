package com.zlz.zojbackendjudgeservice.judge;


import com.zlz.zojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.zlz.zojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.zlz.zojbackendjudgeservice.judge.strategy.JudgeContext;
import com.zlz.zojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.zlz.zojbackendmodel.codesandbox.JudgeInfo;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
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
