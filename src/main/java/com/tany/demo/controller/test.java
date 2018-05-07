package com.tany.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.tany.demo.Utils.ResultModel;
import com.tany.demo.asynctask.AsyncTaskService;
import com.tany.demo.controller.vo.UserInfoEnter;
import com.tany.demo.entity.TestAndSumEntity;
import com.tany.demo.entity.TestEntity;
import com.tany.demo.listener.TestEvent;
import com.tany.demo.redis.RedisService;
import com.tany.demo.service.TestService;
import com.tany.demo.service.UserInfoService;
import com.tany.demo.service.model.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson.JSON;

@RestController
@RequestMapping("tany")
public class test {
    @Autowired
    TestService testService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${kafka.data.topic.engine}")
    private String topic;

    @RequestMapping(value = "/userInfo")
    public String getUserInfo(HttpServletRequest httpRequest){
        UserInfo userInfo = userInfoService.findByUsername("test");
        return "get user info:"+userInfo.toString();
    }

    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     * @return
     */
    @RequestMapping(value = "/unauth")
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }

    /**
     * 登录方法
     * @param userInfoEnter
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String ajaxLogin(UserInfoEnter userInfoEnter) {
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userInfoEnter.getUsername(), userInfoEnter.getPassword());
        try {
            subject.login(token);
            jsonObject.put("token", subject.getSession().getId());
            jsonObject.put("msg", "登录成功");
        } catch (IncorrectCredentialsException e) {
            jsonObject.put("msg", "密码错误");
        } catch (LockedAccountException e) {
            jsonObject.put("msg", "登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            jsonObject.put("msg", "该用户不存在");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest httpRequest){
        return "index page";
    }

    @RequestMapping(path = "/403", method = RequestMethod.GET)
    public String unauthorized(HttpServletRequest httpRequest){
        return "403 page";
    }

    @RequestMapping(path = "/1v2", method = RequestMethod.GET)
    public ResultModel get1v2(HttpServletRequest httpRequest){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("alertId",1L);
        paramMap.put("startTime","2018-04-01 00:00:00");
        paramMap.put("endTime","2018-04-30 00:00:00");

        TestAndSumEntity testAndSumEntity = testService.quary1v2(paramMap);
        return ResultModel.success(testAndSumEntity);
    }

    @RequestMapping(path = "/redis", method = RequestMethod.GET)
    public ResultModel redis(HttpServletRequest httpRequest){
        redisService.set("tany","test");
        return ResultModel.success();
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String list(){
        //查询列表数据
        TestEntity testEntity = testService.queryObject(1L);
        return "mysql:"+testEntity.getDesc();
    }

    @RequestMapping(path = "/event", method = RequestMethod.GET)
    public String event(){
        applicationContext.publishEvent(new TestEvent(this,"1"));
        return "event:"+1;
    }

    @RequestMapping(path = "/async", method = RequestMethod.GET)
    public String async(){
        asyncTaskService.dataTranslate();
        return "asyncTask:"+1;
    }

    @RequestMapping(path = "/kafka", method = RequestMethod.GET)
    public ResultModel send(HttpServletRequest httpRequest) {
        Map<String, String> paramMap = new HashMap<>();
        Map<String, String[]> all = httpRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : all.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue()[0]);
        }

        try {
            kafkaTemplate.send(topic, UUID.randomUUID().toString(), JSON.toJSONString(paramMap));
            return ResultModel.success();
        } catch (Exception e) {
            return ResultModel.operationError();
        }
    }
}
