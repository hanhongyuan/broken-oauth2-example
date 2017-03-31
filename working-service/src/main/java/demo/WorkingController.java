package demo;

import demo.security.oauth2.jwt.RemoteOAuthTokenReusingRestTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WorkingController {
    @Autowired
    private WorkingService workingService;

    @Autowired
    private RemoteOAuthTokenReusingRestTemplateFactory remoteOAuthTokenReusingRestTemplateFactory;

    @RequestMapping("/test")
    public ResponseEntity<String> runTest() throws Exception {
        return Optional.ofNullable(workingService.stringify(getCurrentUser()))
                .map(username -> new ResponseEntity<>(username, HttpStatus.OK))
                .orElseThrow(() -> new Exception("Should not happen."));
    }

    /**
     * The user must be injected into the service because the service is using Hystrix which is currently not able to
     * copy ThreadLocal properties from the request scope into the ThreadLocal state of the spawned thread.
     * <p>
     * This issue is known and there are various (very verbose fixes). I have decided to inject the user from a method
     * which is not wrapped by the circuit breaker as long as Netflix does not provide viable solution. Once there is a
     * solution, this method should be moved to {@link WorkingService}.
     * <p>
     * See also:
     * https://github.com/Netflix/Hystrix/issues/1162
     * https://jmnarloch.wordpress.com/2016/07/06/spring-boot-hystrix-and-threadlocals/
     * https://github.com/spring-cloud/spring-cloud-netflix/pull/1379 (promising solution)
     *
     * @return Currently authenticated user.
     */
    private User getCurrentUser() {
        return remoteOAuthTokenReusingRestTemplateFactory.createRemoteOAuthTokenReusingRestTemplate().getForObject("http://user-service/v1/me", User.class);
    }
}