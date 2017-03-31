package demo.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public void save(User user) {
        user.setId(users.size());
        users.add(user);
    }

    public void delete(User user) {
        if (users.contains(user)) {
            users.remove(user);
        }
    }
}
