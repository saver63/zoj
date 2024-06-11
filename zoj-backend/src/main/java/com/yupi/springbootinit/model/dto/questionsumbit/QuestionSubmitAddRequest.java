package com.yupi.springbootinit.model.dto.questionsumbit;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题目提交表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户代码
     */
    private String code;


    private static final long serialVersionUID = 1L;
}