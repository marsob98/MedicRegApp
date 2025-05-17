package pl.Sobiermann.MedicRegApp.Reservation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.Sobiermann.MedicRegApp.User.User;
import pl.Sobiermann.MedicRegApp.User.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationService reservationService;



    @PostMapping
    public ResponseEntity<String> createReservation(@Valid @RequestBody ReservationDTO reservationDTO,
                                                     @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        LocalDate today = LocalDate.now();
        if (reservationDTO.getAppointmentDate().isBefore(today)) {
            return ResponseEntity.badRequest().body("Appointment date cannot be in the past");
        }


        Reservation reservation = new Reservation();
        reservation.setDescription(reservationDTO.getDescription());
        reservation.setAppointmentTime(reservationDTO.getAppointmentTime());
        reservation.setAppointmentDate(reservationDTO.getAppointmentDate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setUser(user);

        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation created successfully");
    }



    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(res -> new ReservationDTO(
                        res.getId(),
                        res.getDescription(),
                        res.getUser().getUsername(),
                        res.getAppointmentDate(),
                        res.getAppointmentTime(),
                        res.getStatus()))
                        .toList();
    }



    @GetMapping("/my")
    public List<ReservationDTO> getMyReservations(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return List.of();
        }
        return reservationRepository.findByUser(user).stream()
                .map(res -> new ReservationDTO(
                        res.getId(),
                        res.getDescription(),
                        username,
                        res.getAppointmentDate(),
                        res.getAppointmentTime(),
                        res.getStatus()))
                .toList();
    }




    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id,
                                                    @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));


        if (!reservation.getUser().getUsername().equals(username) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't delete this reservation");
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.ok("Reservation deleted");
    }


    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable Long id,
                                                    @RequestBody ReservationDTO reservationDTO,
                                                    @RequestHeader("Authorization") String authHeader) {

        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Reservation not found"));


        if (!reservation.getUser().getUsername().equals(username) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't update this reservation");
        }

        if (reservationDTO.getDescription() != null) {
            reservation.setDescription(reservationDTO.getDescription());
        }

        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation updated");
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status,
            @RequestHeader("Authorization") String authHeader) {

        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (!reservation.getUser().getUsername().equals(username) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot update this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELED && status == ReservationStatus.CONFIRMED) {
            return ResponseEntity.badRequest().body("Cannot change status from CANCELED to CONFIRMED");
        }

        reservation.setStatus(status);
        reservationRepository.save(reservation);

        return ResponseEntity.ok("Reservation status updated to" + status);

    }




    @GetMapping("/search")
    public Page<ReservationDTO> searchReservation(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                  @RequestParam(required = false) ReservationStatus status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "appointmentDate") String sortBy,
                                                  @RequestHeader("Authorization") String authHeader) {

        String username = extractUsernameFromAuthHeader(authHeader);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return Page.empty();
        }

        if (user.getRole().equals("ROLE_ADMIN")) {
            Page<Reservation> reservations = reservationService.searchReservations(
                    startDate, endDate, status, page, size, sortBy);
            return reservations.map(res -> new ReservationDTO(
                    res.getId(),
                    res.getDescription(),
                    res.getUser().getUsername(),
                    res.getAppointmentDate(),
                    res.getAppointmentTime(),
                    res.getStatus()));

        } else {

            Page<Reservation> reservations = reservationService.getPaginatedReservations(page, size, sortBy);
            return reservations.map(res -> new ReservationDTO(
                    res.getId(),
                    res.getDescription(),
                    res.getUser().getUsername(),
                    res.getAppointmentDate(),
                    res.getAppointmentTime(),
                    res.getStatus()));
        }
    }


    private String extractUsernameFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Delete "Basic " and decode Base64
            String base24Credentials = authHeader.substring(6);
            byte[] credDecoded = java.util.Base64.getDecoder().decode(base24Credentials);
            String credentials = new String(credDecoded);
            // Credentials are in format "username:password"
            return credentials.split(":", 2)[0];
        }
        return null;
    }








}
