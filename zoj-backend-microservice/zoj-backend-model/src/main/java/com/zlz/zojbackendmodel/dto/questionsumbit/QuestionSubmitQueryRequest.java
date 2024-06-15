package com.zlz.zojbackendmodel.dto.questionsumbit;

import com.zlz.zojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询题目提交表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 判题状态(0-待判题，1-判题中，2判题成功，3-判题失败)
     */
    private Integer status;

    /**
     * 用户id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}