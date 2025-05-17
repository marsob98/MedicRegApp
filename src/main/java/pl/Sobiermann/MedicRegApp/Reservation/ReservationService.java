package pl.Sobiermann.MedicRegApp.Reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.Sobiermann.MedicRegApp.Reservation.ReservationRepository;
import pl.Sobiermann.MedicRegApp.User.User;
import pl.Sobiermann.MedicRegApp.User.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Reservation> getReservationByUser(User user) {
        return reservationRepository.findByUser(user);
    }


    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }


    public Reservation createReservation(Reservation reservation, User user) {
        if (reservation.getDescription() == null || reservation.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        LocalDate today = LocalDate.now();
        if (reservation.getAppointmentDate() == null || reservation.getAppointmentDate().isBefore(today)) {
            throw new IllegalArgumentException("Appointment date must be in the future");
        }

        if (reservation.getAppointmentTime() == null) {
            throw  new IllegalArgumentException("Appointment time cannot be empty");
        }

        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setUser(user);

        return reservationRepository.save(reservation);
    }


    public void cancelReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalArgumentException("Reservation with ID " + reservationId + " does not exist");
        }

        reservationRepository.deleteById(reservationId);
    }


    public Reservation updateReservation(Long reservationId, Reservation updatedReservation) {
        Reservation existingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(()
                        -> new IllegalArgumentException("Reservation with ID " + reservationId + " does not exist"));

        if (updatedReservation.getDescription() != null) {
            existingReservation.setDescription(updatedReservation.getDescription());
        }

        return reservationRepository.save(existingReservation);
    }


    public Page<Reservation> getPaginatedReservations(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reservationRepository.findAll(pageable);
    }


    public Page<Reservation> searchReservations(
            LocalDate startDate,
            LocalDate endDate,
            ReservationStatus status,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        if (startDate != null && endDate != null && status != null) {
            return reservationRepository.findByAppointmentDateBetweenAndStatus(startDate, endDate, status, pageable);
        } else if (startDate != null && endDate != null) {
            return reservationRepository.findByAppointmentDateBetween(startDate, endDate, pageable);
        } else if (startDate != null && status != null) {
            return reservationRepository.findByAppointmentDateAndStatus(startDate, status, pageable);
        } else if (startDate != null) {
            return reservationRepository.findByAppointmentDate(startDate, pageable);
        } else if (status != null) {
            return reservationRepository.findByStatus(status, pageable);
        } else {
            return reservationRepository.findAll(pageable);
        }
    }


    public Page<Reservation> searchUserReservations(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            ReservationStatus status,
            int page,
            int size,
            String sortBy) {

        List<Reservation> userReservations = reservationRepository.findByUser(user);

        return getPaginatedReservations(page, size, sortBy);
    }




}
