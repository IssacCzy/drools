package com.oristar.service;


import com.oristar.vo.Person;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoService {

    private static final Logger logger = LoggerFactory.getLogger(DoService.class);

    @Autowired
    private KieContainer kieContainer;

    public Person doRules(Person person) {

        logger.info("\r\n 规则匹配前的参数 Fact Object ：\r\n{}", person.toString());

        /**
         * 获取规则引擎会话session
         *   rulesSession：kmodule.xml 中配置
         */
        KieSession kieSession = kieContainer.newKieSession("rulesSession");
        FactHandle factHandle = kieSession.insert(person);
        logger.info("\r\n在 work memory 中的对象描述信息：\r\n{}", factHandle);

        /**
         * 通过insert等方法插入fact，取得一个句柄
         * 通过这个句柄可以多次更新fact从而触发规则
         */
        kieSession.update(factHandle, person);
        fireAllRules(kieSession);

        Person pFact = (Person) kieSession.getObject(factHandle);
        logger.info("\r\n 规则匹配后的参数 FactHandle Object ：\r\n{}", pFact.toString());

        logger.info("\r\n 规则匹配后的参数 Fact Object ：\r\n{}", person.toString());

        return person;
    }

    /**
     * 使用的方法 KnowledgeBase 知识库来动态加载 rule string
     * 但是 KnowledgeBase 这个类过时了
     *
     * @param person
     * @return
     */
    public Person doRules2(Person person) {
        String rule_str = dynamic_concat_ruleStr_2_drl();

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        //以DRL形式加载规则
        builder.add(ResourceFactory.newByteArrayResource(rule_str.getBytes()), ResourceType.DRL);
        KnowledgeBuilderErrors errors = builder.getErrors();
        for (KnowledgeBuilderError error : errors) {
            System.out.println(error.getMessage());
        }
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(builder.getKnowledgePackages());

        //获取规则引擎会话session
        KieSession kieSession = kBase.newKieSession();
        kieSession.insert(person);

        fireAllRules(kieSession);
        return person;
    }

    /**
     * 使用最新的 KieHelper 这个类来动态加载 rule string
     *
     * @param person
     * @return
     */
    public Person doRules3(Person person) {
        String rule_str = dynamic_concat_ruleStr_2_drl();

        KieHelper helper = new KieHelper();
        helper.addContent(rule_str, ResourceType.DRL);

        //获取规则引擎会话session
        KieSession kieSession = helper.build().newKieSession();
        kieSession.insert(person);

        fireAllRules(kieSession);
        return person;
    }

    /**
     * 执行会话规则并关闭会话
     *
     * @param kieSession
     */
    private void fireAllRules(KieSession kieSession) {
        int invoked_count = kieSession.fireAllRules();
        System.out.println("一共执行了【" + invoked_count + "】条规则");
        kieSession.dispose();
    }

    /**
     * 动态拼接 DRL 格式字符串
     *
     * @return
     */
    private String dynamic_concat_ruleStr_2_drl() {

        StringBuilder drl_rule_str = new StringBuilder();
        /*package部分*/
        drl_rule_str.append("package rules\r\n");
        drl_rule_str.append("\r\n");

        /*导包部分*/
        drl_rule_str.append("import com.oristar.vo.Person\r\n");
        drl_rule_str.append("\r\n");

        return dynamic_concat_rules(drl_rule_str);
    }

    private String dynamic_concat_rules(StringBuilder rules_sb) {

        /*规则申明部分*/
        rules_sb.append("rule \"check age1\"\r\n");

        /*规则属性部分*/

        /*规则条件部分*/
        rules_sb.append("\twhen\r\n");
        rules_sb.append("\t\t$person: Person(age <= 10)\r\n");

        /*规则结果部分*/
        rules_sb.append("\tthen\r\n");
        rules_sb.append("\t\t$person.setDes(\"too yong\");\r\n");

        /*规则结束*/
        rules_sb.append("end\r\n");

        /*规则申明部分*/
        rules_sb.append("rule \"check age2\"\r\n");

        /*规则属性部分*/

        /*规则条件部分*/
        rules_sb.append("\twhen\r\n");
        rules_sb.append("\t\t$person: Person(age > 10)\r\n");

        /*规则结果部分*/
        rules_sb.append("\tthen\r\n");
        rules_sb.append("\t\t$person.setDes(\"age is ok\");\r\n");

        /*规则结束*/
        rules_sb.append("end\r\n");

        return rules_sb.toString();
    }
}
