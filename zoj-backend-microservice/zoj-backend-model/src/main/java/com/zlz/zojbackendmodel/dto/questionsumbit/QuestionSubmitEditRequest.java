package com.zlz.zojbackendmodel.dto.questionsumbit;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑题目提交表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionSubmitEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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

    /**
     * 判题信息（Json格式）
     */
    private String judgeInfo;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 判题状态(0-待判题，1-判题中，2判题成功，3-判题失败)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}