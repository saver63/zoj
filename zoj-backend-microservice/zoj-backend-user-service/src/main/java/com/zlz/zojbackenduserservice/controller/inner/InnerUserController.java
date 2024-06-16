package com.zlz.zojbackenduserservice.controller.inner;

import com.zlz.zojbackendmodel.entity.User;
import com.zlz.zojbackendserviceclient.service.UserFeignClient;
import com.zlz.zojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 改服务仅内部调用，不是给前端使用的
 */
@RestController
@RequestMapping("/inner")
public class InnerUserController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId){
        return userService.getById(userId);
    }

    /**
     * 根据id获取用户列表
     *
     * @param idList mybatisplus默认提供的类
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList){
        return userService.listByIds(idList);
    }

}
