package com.wanke.gitcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GitcloudApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(GitcloudApplication.class, args);
        FileCheck fileCheck = applicationContext.getBean(FileCheck.class);
        if (!fileCheck.init0()){
            System.out.println("application.properties的my.rootDirectory属性请设置一个空目录或者曾经用于该存储系统的目录");
            System.exit(0);
        }

    }

}
