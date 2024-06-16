package com.zlz.zojbackendjudgeservice.judge.controller.inner;

import com.zlz.zojbackendjudgeservice.judge.JudgeService;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
import com.zlz.zojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 改服务仅内部调用，不是给前端使用的
 */
@RestController
@RequestMapping("/inner")
public class InnerJudgeController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;


    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }

}
