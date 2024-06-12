package com.yupi.zojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
//构造器创建对象
@Builder
//生成无参的构造函数
@NoArgsConstructor
//生成有所有参数的构造函数
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 输出用例
     */
    private List<String> outputList;

    /**
     * 接口信息(不是程序的执行信息，比如接口执行超时)
     */
    private String message;

    /**
     * 判题状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
