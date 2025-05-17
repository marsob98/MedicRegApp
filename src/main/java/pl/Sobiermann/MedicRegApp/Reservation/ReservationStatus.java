package pl.Sobiermann.MedicRegApp.Reservation;

public enum ReservationStatus {
    PENDING("Oczekująca"),
    CONFIRMED("Potwierdzona"),
    CANCELED("Anulowana"),
    COMPLETED("Zakończona");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
