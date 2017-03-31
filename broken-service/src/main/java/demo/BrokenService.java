package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class BrokenService {
    @LoadBalanced
    @Autowired
    private OAuth2RestTemplate oAuth2RestTemplate;

    @HystrixCommand
    public String stringify() {
        User user = getCurrentUser();
        if (user != null) {
            return String.valueOf(user.getId());
        }
        return null;
    }

    private User getCurrentUser() {
        return oAuth2RestTemplate.getForObject("http://user-service/v1/me", User.class);
    }
}
