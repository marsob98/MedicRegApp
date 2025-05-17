package pl.Sobiermann.MedicRegApp.User;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.Sobiermann.MedicRegApp.Reservation.Reservation;
import pl.Sobiermann.MedicRegApp.Reservation.ReservationDTO;
import pl.Sobiermann.MedicRegApp.Reservation.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReservationService reservationService;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {

        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username aleady exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map
                (user -> new UserDTO(user.getId(), user.getUsername(), user.getRole()))
                .toList();
    }



    @GetMapping("/search")
    public Page<ReservationDTO> searchPaginated(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "id") String sortBy) {

        Page<Reservation> reservations = reservationService.getPaginatedReservations(page, size, sortBy);
        return reservations.map(res -> new ReservationDTO(res.getId(),
                res.getDescription(),
                res.getUser().getUsername(),
                res.getAppointmentDate(),
                res.getAppointmentTime(),
                res.getStatus()));
    }

}
