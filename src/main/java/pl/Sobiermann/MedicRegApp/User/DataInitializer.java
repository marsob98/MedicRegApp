package pl.Sobiermann.MedicRegApp.User;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostConstruct
    public void initializeData() {
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        } else {
            System.out.println("Admin user already exists");
        }

        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("userpass"));
            user.setRole("ROLE_USER");
            userRepository.save(user);
        } else {
            System.out.println("User user already exists");
        }
    }

}
