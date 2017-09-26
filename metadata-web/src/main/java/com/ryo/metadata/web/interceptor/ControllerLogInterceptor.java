package com.ryo.metadata.web.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by houbinbin on 16/6/30.
 * - Controller 访问日志
 */
@Component
@Aspect
@Log4j2
public class ControllerLogInterceptor {

    private String requestPath = null; // 请求地址
    private String userName = null; // 用户名
    private Map<?, ?> inputParamMap = null; // 传入参数
    private Map<?, ?> filterInputParamMap = new HashMap<>(); // 过滤后的传入参数
    private Map<String, Object> outputParamMap = null; // 存放输出结果


    @Pointcut("execution(public * com.ryo.doc.web.controller..*.*(..))")
    public void myPointcut() {
    }

    @Before("myPointcut()")
    public void start() {
    }

    @After("myPointcut()")
    public void end() {
        printLog();
    }

    @Around("myPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        /**
         * 1.获取request信息
         * 2.根据request获取session
         * 3.从session中取出登录用户信息
         */
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        userName = StringUtils.EMPTY;

        // 获取输入参数
        inputParamMap = request.getParameterMap();

        // 获取请求地址
        requestPath = request.getRequestURI();

        //不打印特殊字段
//        filterInputParamMap = filterInputParamMap(inputParamMap, requestPath);

        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        outputParamMap = new HashMap<>();
        Object result = point.proceed();    // result的值就是被拦截方法的返回值
        outputParamMap.put("result", result);

        return result;
    }

    private void printLog() {
        log.info("USER：" + userName
                + "  URL：" + requestPath + "\n"
                + "PARAM：" + JSON.toJSON(filterInputParamMap) + "\n" + "RESULT：" + JSON.toJSON(outputParamMap)+"\n");
    }


    /**
     * 忽略掉密码字段
     * - inputParamMap 是不可变的
     */
//    private Map<?,?> filterInputParamMap(Map<?,?> inputParamMap, String requestPath) {
//        Map<?, ?> filterInputParamMap = new HashMap<>(); // 过滤后的传入参数
//
//        if("/login".equals(requestPath) && MapUtils.isNotEmpty(inputParamMap)) {
//            filterInputParamMap = new HashMap<>(inputParamMap);
//            filterInputParamMap.remove("password");
//            return filterInputParamMap;
//        }
//        if("/user/password".equals(requestPath)) {
//            return filterInputParamMap;
//        }
//        return inputParamMap;
//    }

}
