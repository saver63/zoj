package com.yupi.zojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.yupi.zojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.zojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.zojcodesandbox.model.ExecuteMessage;
import com.yupi.zojcodesandbox.model.JudgeInfo;
import com.yupi.zojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String DEFAULT_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;


    /**
     * 代码沙箱执行模板方法
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        System.setSecurityManager(new DenySecurityManager());

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        //1.把用户的代码保存为文件
        File userCodeFIle = saveCodeFile(code);

        //2.编译代码。得到class文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFIle);
        System.out.println(compileFileExecuteMessage);

        //3.创建容器，把容器放到文件内
        List<ExecuteMessage> executeMessages = runFile(userCodeFIle, inputList);


        //4.收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessages);

        //5.文件清理
        boolean b = deleteFile(userCodeFIle);
        if(!b){
            log.error("deleteFile Error,userCodeFilePath = {}",userCodeFIle.getAbsolutePath());
        }

        return outputResponse;
    }

    /**
     * 1.把用户的代码保存为文件
     * @Param code 用户代码
     * @return
     */
    public File saveCodeFile(String code){
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator +GLOBAL_CODE_DIR_NAME;

        //判断全局diamagnetic目录是否存在，没有则新建,有线程安全，不应该反复执行，放在全局共享类
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + DEFAULT_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2.编译代码。得到class文件
     * @Param userCodeFile
     * @return
     */
    private ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitCode() != 0){
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
//            return getErrorResponse(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 3.执行文件，获得执行文件列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList){
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        // 3.执行程序,目录动态，输入用例动态
        List<ExecuteMessage> executeMessagesList = new ArrayList<>();
        for (String inputArgs : inputList) {
//            String rumCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s",userCodeParentPath,SECURITY_MANAGER_CLASS_PATH,SECURITY_MANAGER_CLASS_NAME,inputArgs);
            String rumCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s",userCodeParentPath,inputArgs);


            try {
                Process runProcess = Runtime.getRuntime().exec(rumCmd);
                new Thread(()->{
                    //超时控制
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessagesList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("程序执行异常",e);
            }
        }
        return  executeMessagesList;
    }

    /**
     * 4.获取输出结果
     * @param executeMessagesList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessagesList){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        List<String> outputList = new ArrayList<>();
        //取用时最大值，便于判断是否超时
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessagesList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)){
                executeCodeResponse.setMessage(errorMessage);
                //用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null){
                maxTime = Math.max(maxTime,time);
            }
        }

        executeCodeResponse.setOutputList(outputList);
        //正常执行
        if (outputList.size() == executeMessagesList.size()){
            executeCodeResponse.setStatus(1);
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        //内存要第三方库来获取内存占用，自己实现非常麻烦，此处不做实现
//        judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return  executeCodeResponse;
    }

    /**
     * 5.删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile){
        if (userCodeFile.getParentFile()!=null){
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del ? "成功" : "失败"));
            return del;
        }
        return true;
    }


    /**
     * 6.获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        //2表示代码沙箱错误，表示本系统执行过程中的错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
