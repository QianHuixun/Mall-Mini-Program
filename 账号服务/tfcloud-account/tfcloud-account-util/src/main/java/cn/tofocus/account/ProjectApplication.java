package cn.tofocus.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "cn.tofocus")
@ComponentScan(basePackages = {"cn.tofocus"})
@SpringBootApplication
public class ProjectApplication
{
    static
    {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
    
    public static void main(String[] args)
    {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
