package com.zlz.zojbackendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zlz.zojbackendmodel.dto.questionsumbit.QuestionSubmitAddRequest;
import com.zlz.zojbackendmodel.dto.questionsumbit.QuestionSubmitQueryRequest;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
import com.zlz.zojbackendmodel.entity.User;
import com.zlz.zojbackendmodel.vo.QuestionSubmitVO;

/**
 * 题目提交表服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {



    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目创建信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);


    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目表封装（单条）
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目表封装（分页）
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
