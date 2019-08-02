package com.oristar.controller;


import com.oristar.vo.Person;
import com.oristar.service.DoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrlFileLoadRules2WorkMemoryController {

    private static final Logger logger = LoggerFactory.getLogger(DrlFileLoadRules2WorkMemoryController.class);

    @Autowired
    private DoService doService;

    /**
     * 使用 DRL 文件的形式来加载规则,初始化规则引擎实例
     *
     * @param age
     * @param sex
     * @return
     */
    @RequestMapping(value = "/do", method = RequestMethod.GET, produces = "application/json")
    public Person getPersonInfo(@RequestParam(required = true) int age, @RequestParam(required = true) int sex) {
        Person person = new Person(age, sex);
        doService.doRules(person);
        return person;
    }


    /**
     * 使用知识库 KnowledgeBase 来动态加载规则到 work memory
     * <p>
     * 动态拼接 drl string
     * <p>
     * 已过时
     *
     * @param age
     * @param sex
     * @return
     */
    @RequestMapping(value = "/do2", method = RequestMethod.GET, produces = "application/json")
    public Person dynamic_init_rule_engine(@RequestParam(required = true) int age, @RequestParam(required = true) int sex) {
        Person person = new Person(age, sex);
        doService.doRules2(person);
        return person;
    }

    /**
     * 使用 KieHelper 来动态加载规则到 work memory
     * <p>
     * 动态拼接 drl string
     *
     * @param age
     * @param sex
     * @return
     */
    @RequestMapping(value = "/do3", method = RequestMethod.GET, produces = "application/json")
    public Person dynamic_init_rule_engine3(@RequestParam(required = true) int age, @RequestParam(required = true) int sex) {
        Person person = new Person(age, sex);
        doService.doRules3(person);
        return person;
    }
}
