package com.yupi.springbootinit.judge.strategy;

import com.yupi.springbootinit.model.dto.questionsumbit.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {


    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
