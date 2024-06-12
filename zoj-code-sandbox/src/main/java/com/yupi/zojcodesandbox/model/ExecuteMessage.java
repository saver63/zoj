package com.yupi.zojcodesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 正常输出信息
     */
    private String message;

    /**
     * 错误输出信息
     */
    private String errorMessage;

    /**
     * 程序用时
     */
    private Long time;
}
