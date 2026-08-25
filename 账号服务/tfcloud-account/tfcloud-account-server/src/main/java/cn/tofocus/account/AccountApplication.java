package cn.tofocus.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年3月29日]
 */
@EnableFeignClients(basePackages = "cn.tofocus")
@ComponentScan(basePackages = {"cn.tofocus"})
@SpringBootApplication
@EnableTransactionManagement
@EntityScan({"cn.tofocus.domain", "cn.tofocus.account.db.entity"})
public class AccountApplication
{
    
    public static void main(String[] args)
    {
        SpringApplication.run(AccountApplication.class, args);
    }
    
    @Bean
    public RequestContextListener requestContextListener()
    {
        return new RequestContextListener();
    }
    
    @Bean
    @LoadBalanced
    RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
    
}
