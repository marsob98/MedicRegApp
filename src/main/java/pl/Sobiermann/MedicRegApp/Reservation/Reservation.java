package pl.Sobiermann.MedicRegApp.Reservation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.Sobiermann.MedicRegApp.User.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Opis jest wymagany")
    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String description;


    @NotNull(message = "Data wizyty jest wymagana")
    @Future(message = "Data wizyty musi być w przyszłości")
    private LocalDate appointmentDate;


    @NotNull(message = "Godzina wizyty jest wymagana")
    private LocalTime appointmentTime;


    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reservation() {

    }


}
