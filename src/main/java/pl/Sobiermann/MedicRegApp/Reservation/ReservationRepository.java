package pl.Sobiermann.MedicRegApp.Reservation;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.Sobiermann.MedicRegApp.User.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);

    List<Reservation> findByAppointmentDate(LocalDate date);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByAppointmentDateAndStatus(LocalDate date, ReservationStatus status);

    List<Reservation> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    List<Reservation> findByAppointmentDateBetweenAndStatus(
            LocalDate startDate, LocalDate endDate, ReservationStatus status);

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    Page<Reservation> findByAppointmentDate(LocalDate date, Pageable pageable);

    Page<Reservation> findByAppointmentDateBetween(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Reservation> findByAppointmentDateAndStatus(
            LocalDate date, ReservationStatus status, Pageable pageable);

    Page<Reservation> findByAppointmentDateBetweenAndStatus(
            LocalDate startDate, LocalDate endDate, ReservationStatus status, Pageable pageable);



    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.appointmentDate = :date " +
           "AND r.appointmentTime = :time " +
           "AND (r.status = pl.Sobiermann.MedicRegApp.Reservation.ReservationStatus.PENDING " +
           "OR r.status = pl.Sobiermann.MedicRegApp.Reservation.ReservationStatus.CONFIRMED)")
    boolean existsByDateAndTime(@Param("date") LocalDate date, @Param("time")LocalTime time);

    List<Reservation> findByAppointmentDateAndStatusIn(
            LocalDate date,
            List<ReservationStatus> statuses);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.appointmentDate = :date " +
           "AND r.appointmentTime >= :startTime " +
           "AND r.appointmentTime <= :endTime " +
           "AND (r.status = pl.Sobiermann.MedicRegApp.Reservation.ReservationStatus.PENDING " +
           "OR r.status = pl.Sobiermann.MedicRegApp.Reservation.ReservationStatus.CONFIRMED)")
    boolean existsByDateAndTimeRange(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);


}
