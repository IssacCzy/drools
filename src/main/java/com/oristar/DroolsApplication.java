package com.oristar;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = "com.oristar.mapper")
public class DroolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DroolsApplication.class, args);

    }

    /*需要注入这个类到bean工厂*/
    @Bean
    public KieContainer kieContainer() {
        //默认自动加载 META-INF/kmodule.xml
        return KieServices.Factory.get().getKieClasspathContainer();
    }

}
