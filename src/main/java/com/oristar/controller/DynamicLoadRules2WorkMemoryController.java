package com.oristar.controller;

import com.oristar.service.RuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 规则引擎动态加载及调用
 * <p>
 * 这里其实只是为了方便测试，实际上
 * loadRule 应该是服务器必须启动一次的，而loadRule则是配套的后台系统对数据库的规则进行了操作之后调用一次
 * useRule 则是客户端通过消息中间件触发，而不应该暴露出来
 */
@RestController
public class DynamicLoadRules2WorkMemoryController {

    private static final Logger logger = LoggerFactory.getLogger(DynamicLoadRules2WorkMemoryController.class);


    @Autowired
    RuleService ruleService;

    /**
     * 数据库获取规则数据，根据规则模版生成drl规则文件到内存
     *
     * @return
     */
    @RequestMapping(value = "/loadRule", method = RequestMethod.GET, produces = "application/json")
    public String getActivityRule() {
        List<String> drl_strs = ruleService.loadRule();
        logger.info("\r\n依据模版规则生成的 drl str : \r\n{}", drl_strs);
        return drl_strs.toString();
    }

    /**
     * 调用 loadRule 方法加载到 work memory 中的规则
     *
     * @return
     */
    @RequestMapping("/useRule")
    public String useRule() {
        ruleService.useRule("123456", "13712750166");
        return "SUCCESS";
    }

    /**
     * 调用 loadRule 方法加载到 work memory 中的规则
     *
     * @return
     */
    @RequestMapping("/useRule2")
    public String useRule2() {
        ruleService.useRule("123456", "13712750156");
        return "SUCCESS";
    }
}
