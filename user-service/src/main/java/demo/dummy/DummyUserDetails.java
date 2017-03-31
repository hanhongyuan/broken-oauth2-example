package demo.dummy;

import org.springframework.security.core.authority.AuthorityUtils;

public class DummyUserDetails extends org.springframework.security.core.userdetails.User {
    public DummyUserDetails(String username, String password) {
        super(username, password, AuthorityUtils.NO_AUTHORITIES);
    }
}
