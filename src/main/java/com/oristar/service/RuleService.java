package com.oristar.service;

import com.alibaba.fastjson.JSON;
import com.oristar.annotation.Fact;
import com.oristar.domain.ActivityRule;
import com.oristar.domain.RegisterMqDTO;
import com.oristar.domain.RuleDTO;
import com.oristar.domain.RuleExecutorResult;
import com.oristar.domain.fact.BaseFact;
import com.oristar.domain.fact.RegisterFact;
import com.oristar.executor.RuleExecutor;
import com.oristar.generator.RuleGenerator;
import com.oristar.mapper.ActivityRuleMapper;
import com.oristar.util.CopyUtil;
import com.oristar.vo.Person;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RuleService {

    public static final Logger log = LoggerFactory.getLogger(RuleService.class);

    @Autowired
    ActivityRuleMapper activityRuleMapper;

    /**
     * 加载规则
     *
     * @return 返回 List<drl_str>
     */
    public List<String> loadRule() {
        try {
            List<RuleDTO> ruleDTOs = getActivityRuleList();
            log.info("\r\n{}条加入规则引擎", ruleDTOs.size());
            if (!ruleDTOs.isEmpty()) {
                RuleGenerator generator = new RuleGenerator();
                return generator.generateRules(ruleDTOs);
            }
        } catch (Exception e) {
            log.error("RuleService.loadRule。e={}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 触发规则
     */
    public void useRule(String userId, String phone) {
        //生成初始的baseFact
        BaseFact fact = new BaseFact(userId);

        //因为是uuid所以修改了的规则，重载加载是新的drl，故从数据库动态加载之时，is_delete属性要注意
        String orderId = UUID.randomUUID().toString();

        //getActivityRuleList
        RegisterMqDTO domain = new RegisterMqDTO();
        domain.setTelephone(phone);
        try {
            /*可以知道一条信息，匹配了多少个规则，成功了几个*/
            RuleExecutorResult ruleExecutorResult = beforeExecute(orderId, fact, domain);
            log.info("RuleService|useRule|ruleExecutorResult={}", JSON.toJSON(ruleExecutorResult));
        } catch (Exception e) {
            log.error("RuleService|useRule|class={},orderId={}, userId={}, 规则执行异常:{}", this.getClass().getName(), orderId, "123456789", e.getMessage(), e);
        }
    }

    /**
     * 执行前
     */
    public RuleExecutorResult beforeExecute(String orderId, BaseFact fact, RegisterMqDTO domain) {
        RegisterFact registerFact = buildRegisterFact(domain);
        CopyUtil.copyPropertiesCglib(fact, registerFact);
        log.info("RuleService|beforeExecute|{}事件的orderId={}, RegisterMqDTO={}", registerFact.getClass().getAnnotation(Fact.class).value(), orderId, domain);
        return RuleExecutor.execute(registerFact, orderId);
    }

    /**
     * 生成初始的registerFact
     */
    private RegisterFact buildRegisterFact(RegisterMqDTO domain) {
        RegisterFact registerFact = new RegisterFact();

        CopyUtil.copyPropertiesCglib(domain, registerFact);
        return registerFact;
    }

    /**
     * useRule
     * 从数据库里面取规则
     */
    public List<RuleDTO> getActivityRuleList() {
        Date begin = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        List<ActivityRule> list = selectAll();
        List<RuleDTO> ruleDTOList = new ArrayList<>();
        for (ActivityRule dto : list) {
            RuleDTO ruleDTO = new RuleDTO();
            ruleDTO.setBeginTime(begin);
            ruleDTO.setEndTime(end);
            ruleDTO.setRule(dto);
            ruleDTOList.add(ruleDTO);
        }
        return ruleDTOList;
    }


    public void loadIssacTemplateRule() {
        //一种模板调用的方式
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        List<Person> personList = new ArrayList<Person>();

        /**
         * 一个javabean  bean中的属性与模板对应，用于赋值
         *
         */
        Person person = null;
        for (int i = 1; i <= 3; i++) {
            person = new Person(25 * i, i > 2 ? 0 : 1);
            personList.add(person);
        }

        //成后的drl文件内容
        String rule_str = objectDataCompiler.compile(personList, Thread.currentThread().getContextClassLoader().getResourceAsStream("test_issac_rule_template.drt"));
        System.out.println(rule_str);

        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.generateAndWritePomXML(RuleExecutor.getReleaseId());
        kieFileSystem.write("src/main/resources/" + UUID.randomUUID() + ".drl", rule_str);

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            log.error("create rule in kieFileSystem Error", kb.getResults());
            throw new IllegalArgumentException("生成规则文件失败");
        }
        System.out.println("---------- 规则加载结束 -------------");

        /**
         * 触发规则
         * KieHelper 是 Drools 提供的工具类，可用于编译 DRL 规则文件，新建会话等
         */
        KieHelper kieHelper = new KieHelper();
        KieSession kieSession = kieHelper.build().newKieSession();
        person = new Person(35, 0);
        kieSession.insert(person);
        int n = kieSession.fireAllRules();
        System.out.println("执行了" + n + "次规则");
        kieSession.dispose();
    }

    private List<ActivityRule> selectAll() {
        return activityRuleMapper.selectAll();
    }

    private ActivityRule selectByPrimaryKey() {
        return activityRuleMapper.selectByPrimaryKey(1);
    }

}
