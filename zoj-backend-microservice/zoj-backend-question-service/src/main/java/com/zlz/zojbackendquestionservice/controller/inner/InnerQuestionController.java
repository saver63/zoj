package com.zlz.zojbackendquestionservice.controller.inner;

import com.zlz.zojbackendmodel.entity.Question;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
import com.zlz.zojbackendquestionservice.service.QuestionService;
import com.zlz.zojbackendquestionservice.service.QuestionSubmitService;
import com.zlz.zojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 改服务仅内部调用，不是给前端使用的
 */
@RestController
@RequestMapping("/inner")
public class InnerQuestionController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;


    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }

}
