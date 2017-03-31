package demo.dummy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class DummyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private UserDetailsManager dummyUserDetailsManager;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (!dummyUserDetailsManager.userExists(username)) {
            return createUser(username, authentication);
        }

        return dummyUserDetailsManager.loadUserByUsername(username);
    }

    private UserDetails createUser(String username, UsernamePasswordAuthenticationToken authentication) {
        DummyUserDetails dummyUserDetails = new DummyUserDetails(username, String.valueOf(authentication.getCredentials()));
        dummyUserDetailsManager.createUser(dummyUserDetails);
        return dummyUserDetails;
    }
}
