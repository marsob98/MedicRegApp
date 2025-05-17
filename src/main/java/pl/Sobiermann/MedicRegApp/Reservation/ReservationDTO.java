package pl.Sobiermann.MedicRegApp.Reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationDTO {

    private Long id;

    @NotBlank(message = "Opis jest wymagany")
    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String description;

    private String username;

    @NotNull(message = "Data wizyty jest wymagana")
    @Future(message = "Data wizyty musi być w przyszłości")
    private LocalDate appointmentDate;

    @NotNull(message = "Godzina wizyty jest wymagana")
    private LocalTime appointmentTime;

    private ReservationStatus status;

    public ReservationDTO(Long id, String description, String username, LocalDate appointmentDate, LocalTime appointmentTime, ReservationStatus status) {
        this.id = id;
        this.description = description;
        this.username = username;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public ReservationDTO() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
