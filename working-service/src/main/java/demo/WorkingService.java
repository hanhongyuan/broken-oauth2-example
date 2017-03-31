package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

@Service
public class WorkingService {
    @HystrixCommand
    public String stringify(User user) {
        if (user != null) {
            return String.valueOf(user.getId());
        }
        return null;
    }
}
