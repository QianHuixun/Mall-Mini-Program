package cn.tofocus.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;

@EnableFeignClients(basePackages = "cn.tofocus")
@ComponentScan(basePackages = {"cn.tofocus"})
@SpringBootApplication
@EnableScheduling
@EntityScan({"cn.tofocus.file.db.entity"})
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public RequestContextListener requestContextListener()
    {
        return new RequestContextListener();
    }
}
