package com.yupi.zojcodesandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.yupi.zojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.zojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.zojcodesandbox.model.ExecuteMessage;
import com.yupi.zojcodesandbox.model.JudgeInfo;
import com.yupi.zojcodesandbox.utils.ProcessUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JavaDockerCodeSandboxOld implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String DEFAULT_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;

    private static final String SECURITY_MANAGER_CLASS_PATH = "F:\\迅雷下载\\work\\project\\oj\\zoj-code-sandbox\\src\\main\\resources\\security";

    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManger";

    private static final Boolean FIRST_INIT=true;

    public static void main(String[] args) {
        JavaDockerCodeSandboxOld javaNativeCodeSandbox = new JavaDockerCodeSandboxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2","1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFIleError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        System.setSecurityManager(new DenySecurityManager());

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        //1.把用户的代码保存为文件
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

        //2.编译代码。得到class文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        } catch (Exception e) {
            return getErrorResponse(e);
        }


        //3.创建容器，把容器放到文件内
        //获取默认的DockerClient
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        //拉取镜像
        String image = "openjdk:8-alpine";
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        //如果是第一次执行则拉取镜像
        if (FIRST_INIT){
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                //每执行一步调用
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像："+item.getStatus());
                    super.onNext(item);
                }
            };
            //异步执行
            try {
                pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("下载完成");
        }

        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100*1000*1000L);
        //减少IO的频率，一定程度上增加稳定性
        hostConfig.withMemorySwap(1L);
        hostConfig.withCpuCount(1L);
        //todo 安全管理配置字符串
        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        hostConfig.setBinds(new Bind(userCodePath,new Volume("/app")));


        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                //限制用户不能向root根目录写文件
                .withReadonlyRootfs(true)
                //网络拒绝
                .withNetworkDisabled(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();


        //启动容器
        dockerClient.startContainerCmd(containerId).exec();

        //docker exec tender_cray java -cp /app Main 1 3
        //执行命令并返回结果
        List<ExecuteMessage> executeMessagesList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java","-cp","Main","1","3"},inputArgsArray) ;
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令"+execCreateCmdResponse);
            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            //默认设置为超时
            final boolean[] timeout = {true};
            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {


                @Override
                public void onComplete() {
                    //如果执行完成则表示没超时
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)){
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("输出错误结果"+ errorMessage[0]);
                    }else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果"+ message[0]);
                    }
                    super.onNext(frame);
                }
            };

            final long[] maxMemory = {0L};

            //获取占用的内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }


                @Override
                public void onStart(Closeable closeable) {

                }


                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });

            statsCmd.exec(statisticsResultCallback);
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getTotalTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessagesList.add(executeMessage);
        }



        //4.收集整理输出结果
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

        //5.文件清理
        if (userCodeFile.getParentFile()!=null){
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del ? "成功" : "失败"));
        }

        return executeCodeResponse;
    }

    /**
     * 获取错误响应
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
