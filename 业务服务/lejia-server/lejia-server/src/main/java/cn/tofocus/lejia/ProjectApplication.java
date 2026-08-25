package cn.tofocus.lejia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;



@EnableFeignClients(basePackages = "cn.tofocus")
@ComponentScan(basePackages = {"cn.tofocus"})
@SpringBootApplication
@EnableScheduling
public class ProjectApplication
{
    
    public static void main(String[] args)
    {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
