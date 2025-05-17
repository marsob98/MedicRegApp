package pl.Sobiermann.MedicRegApp.User;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.Sobiermann.MedicRegApp.User.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
