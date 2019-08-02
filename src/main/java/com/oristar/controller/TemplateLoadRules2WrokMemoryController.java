package com.oristar.controller;

import com.oristar.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试自定义 rule 模版加载
 */
@RestController
public class TemplateLoadRules2WrokMemoryController {

    @Autowired
    RuleService ruleService;

    /**
     * 加载自定义规则模版到 work memory
     * @return
     */
    @RequestMapping(value = "/loadIssacRule", method = RequestMethod.GET, produces = "application/json")
    public String getActivityRule() {
        ruleService.loadIssacTemplateRule();
        return "SUCCESS";
    }
}
