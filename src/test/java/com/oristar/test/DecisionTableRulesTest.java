package com.oristar.test;

import com.oristar.vo.Student;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 决策表使用 Demo
 *
 * @Author Issac
 * @Description TODO
 * @Date 2019/8/5 13:25
 * @Version 1.0
 **/
public class DecisionTableRulesTest {
    private static final Logger logger = LoggerFactory.getLogger(DecisionTableRulesTest.class);

    @Autowired
    KieContainer kieContainer;

    /**
     * 对决策表进行解析，生成 drl 文件
     * <p>
     * 可用于对决策表的正确性进行检查
     *
     * @throws FileNotFoundException
     */
    @Test
    public void compile() throws FileNotFoundException {
        File file = new File(
                "E:\\Projects\\drools\\src\\main\\resources\\META-INF\\decisionTable\\PersonAgeKB.xls");
        InputStream is = new FileInputStream(file);
        SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile(is, InputType.XLS);
        System.out.println("\n\n" + drl);
    }


    /**
     * 规则如下：
     * 年龄 age <=12       少年
     * 年龄 age >12 && age <=24    青年
     * 年龄  age >24 &&age <=65   中年
     * 年龄 age >65    老年
     *
     * @throws FileNotFoundException
     */
    @Test
    public void checkRules() throws FileNotFoundException {

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kc = kieServices.getKieClasspathContainer();
        KieSession ks = kc.newKieSession("PersonAgeKS");

        Student p1 = new Student("奥巴马", 68);
        Student p2 = new Student("普京", 32);
        Student p3 = new Student("朴槿惠", 18);
        Student p4 = new Student("川普", 10);
        Student p5 = new Student("金正恩", 66);

        System.out.println("before p1 : " + p1);
        System.out.println("before p2 : " + p2);
        System.out.println("before p3 : " + p3);
        System.out.println("before p4 : " + p4);
        System.out.println("before p5 : " + p5);
        ks.insert(p1);
        ks.insert(p2);
        ks.insert(p3);
        ks.insert(p4);
        ks.insert(p5);

        int count = ks.fireAllRules();

        System.out.println("总执行了" + count + "条规则------------------------------");
        System.out.println("after p1 : " + p1);
        System.out.println("after p2 : " + p2);
        System.out.println("after p3 : " + p3);
        System.out.println("after p4 : " + p4);
        System.out.println("after p4 : " + p5);
        ks.dispose();
    }
}
