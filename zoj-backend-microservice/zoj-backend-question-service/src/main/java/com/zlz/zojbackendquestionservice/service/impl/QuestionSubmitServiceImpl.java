package com.zlz.zojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlz.zojbackendcommon.common.ErrorCode;
import com.zlz.zojbackendcommon.constant.CommonConstant;
import com.zlz.zojbackendcommon.exception.BusinessException;
import com.zlz.zojbackendcommon.utils.SqlUtils;
import com.zlz.zojbackendmodel.dto.questionsumbit.QuestionSubmitAddRequest;
import com.zlz.zojbackendmodel.dto.questionsumbit.QuestionSubmitQueryRequest;
import com.zlz.zojbackendmodel.entity.Question;
import com.zlz.zojbackendmodel.entity.QuestionSubmit;
import com.zlz.zojbackendmodel.entity.User;
import com.zlz.zojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.zlz.zojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.zlz.zojbackendmodel.vo.QuestionSubmitVO;
import com.zlz.zojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.zlz.zojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.zlz.zojbackendquestionservice.service.QuestionService;
import com.zlz.zojbackendquestionservice.service.QuestionSubmitService;
import com.zlz.zojbackendserviceclient.service.JudgeFeignClient;
import com.zlz.zojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * 题目提交表服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    //出现循环依赖，使用@Lazy懒加载注解
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return 提交记录id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交
        // 锁必须要包裹住事务方法
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        //设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //发送消息
        myMessageProducer.sendMessage("coce_exchange","my_routingKey",String.valueOf(questionSubmitId));
//        //执行判题服务实现
//        CompletableFuture.runAsync(()->{
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmitId;
    }


    /**
     * 获取查询条件（用户根据哪些字段查询，根据前端传来的请求对象，得到mybatis框架支持的查询类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {




        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        //  从对象中取值
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目表封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 对象转封装类
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //脱敏：仅本人和管理员能看见自己（提交userId和登录用户id不同）提交的代码
        long userId = loginUser.getId();
        if (userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页获取题目表封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser){
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        //如下代码存在性能浪费：调用一次getQuestionSubmitVO，都会去查一次数据库或者redis(根据request查用户)
        //1.把查询当前用户写在controller 2.使用ThreadLocal去获取信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;

//        // 对象列表 => 封装对象列表
//        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
//            return QuestionSubmitVO.objToVo(questionSubmit);
//        }).collect(Collectors.toList());
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        //原因：执行多个get操作查多条数据会比一次性分页获取然后再分配慢很多
//        //查询多条题目对应的多条用户信息：先把用户提交的信息放到列表里
//        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
//        //根据多条Id去查用户表，得到用户的集合，再根据Id做分组，得到每个id对应的用户信息
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//        // 再根据id去跟原有的用户消息去匹配
//        questionSubmitVOList.forEach(questionSubmitVO -> {
//            Long userId = questionSubmitVO.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            //再进行填充
//            questionSubmitVO.setUserVO(userService.getUserVO(user));
//        });
        // endregion


    }


}
