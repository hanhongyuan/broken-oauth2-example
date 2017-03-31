package demo.security.oauth2.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.client.RestTemplate;

/**
 * Please note that you need to set {@code ribbon.http.client.enabled=true} in order to use this class since
 * {@link RibbonClientHttpRequestFactory} is disabled by default because it uses the deprecated Ribbon HTTP client.
 * <p>
 * See also:
 * https://github.com/spring-cloud/spring-cloud-netflix/issues/961
 */
public class RemoteOAuthTokenReusingRestTemplateFactory {
    @Autowired
    private RibbonClientHttpRequestFactory ribbonClientHttpRequestFactory;

    @Autowired
    private OAuth2ProtectedResourceDetails resource;

    /**
     * Spring OAuth2 does not relay JWT tokens to the {@link OAuth2RestTemplate}. That's why a custom
     * {@link RestTemplate} is necessary that uses the current request's JWT access token that resides in
     * {@link OAuth2ProtectedResourceDetails}. Additionally, the {@link LoadBalanced} annotation cannot be used because
     * it would create a {@link java.lang.reflect.Proxy} that cannot be cast to {@link RestTemplate}. To use Ribbon's
     * load balanced service resolution, we need to add the {@link RibbonClientHttpRequestFactory} bean.
     * <p>
     * See also:
     * https://github.com/absolutegalaber/jwt-oauth2-example
     * https://github.com/spring-cloud/spring-cloud-security/issues/85
     * https://github.com/spring-cloud/spring-cloud-security/issues/93
     * http://stackoverflow.com/questions/28544055/spring-security-oauth2-userredirectrequiredexception
     *
     * @return A load balanced {@link OAuth2RestTemplate} that relays JWT tokens.
     */
    public RestTemplate createRemoteOAuthTokenReusingRestTemplate() {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(details.getTokenValue());
        OAuth2ClientContext clientContext = new DefaultOAuth2ClientContext(accessToken);
        RestTemplate restTemplate = new OAuth2RestTemplate(resource, clientContext);
        restTemplate.setRequestFactory(ribbonClientHttpRequestFactory);
        return restTemplate;
    }
}
