package demo.dummy;

import demo.user.User;
import demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DummyUserDetailsManager implements UserDetailsManager {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void createUser(UserDetails userDetails) {
        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        userRepository.findByUsername(userDetails.getUsername())
                .ifPresent(user -> userRepository.save(user.setPassword(userDetails.getPassword())));
    }

    @Override
    public void deleteUser(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> userRepository.delete(user));
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetails> potentialUserDetails = userRepository.findByUsername(username)
                .map(user -> new DummyUserDetails(user.getUsername(), user.getPassword()));
        if (potentialUserDetails.isPresent()) {
            return potentialUserDetails.get();
        }
        throw new UsernameNotFoundException("Could not find principal ID.");
    }

}
