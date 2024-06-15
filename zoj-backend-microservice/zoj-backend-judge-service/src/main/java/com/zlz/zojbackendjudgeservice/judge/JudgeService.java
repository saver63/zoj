package com.zlz.zojbackendjudgeservice.judge;


import com.zlz.zojbackendmodel.entity.QuestionSubmit;

/**
 * 判题相关代码：为微服务做准备
 * 判题服务
 */
public interface JudgeService {

    QuestionSubmit doJudge(long questionSubmitId);
}
