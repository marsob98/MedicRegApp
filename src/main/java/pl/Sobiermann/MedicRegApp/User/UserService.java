package pl.Sobiermann.MedicRegApp.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.Sobiermann.MedicRegApp.User.UserRepository;
import pl.Sobiermann.MedicRegApp.User.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id:" + id));
    }

    public User registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("User already exists!");
        }

        return userRepository.save(user);
    }
}
