package demo;

import demo.security.oauth2.jwt.RemoteOAuthTokenReusingRestTemplateFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer
@EnableOAuth2Client
@EnableHystrix
public class WorkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkingApplication.class, args);
    }

    @Bean
    public RemoteOAuthTokenReusingRestTemplateFactory remoteOAuthTokenReusingRestTemplateFactory() {
        return new RemoteOAuthTokenReusingRestTemplateFactory();
    }
}
