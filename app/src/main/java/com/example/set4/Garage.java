package com.example.set4;

/**
 * Represents a physical garage slot in the rental system.
 *
 * At most one Parkable vehicle may occupy a garage at a time.
 * When empty, parkedVehicle is null.
 */
public class Garage {

    /** Garage number (1-based), assigned at construction and never changed. */
    private final int number;

    /** The vehicle currently parked here, or null if the garage is empty. */
    private Parkable parkedVehicle;

    /**
     * Constructs a new, empty garage with the given number.
     *
     * @param number positive integer identifying this garage (e.g. 1..N)
     */
    public Garage(int number) {
        this.number        = number;
        this.parkedVehicle = null;
    }

    // ─── Required API ────────────────────────────────────────────────────────

    /** Returns the garage number. */
    public int getNumber() {
        return number;
    }

    /** Returns true if no vehicle is currently parked in this garage. */
    public boolean isEmpty() {
        return parkedVehicle == null;
    }

    /** Returns the vehicle parked in this garage, or null if empty. */
    public Parkable getParkedVehicle() {
        return parkedVehicle;
    }

    // ─── Package-level mutators (called only by Car / Bicycle park/unpark) ───

    /**
     * Associates a vehicle with this garage (called from Parkable.park()).
     * This does NOT enforce business rules — enforcement is done in the vehicle.
     */
    void setParkedVehicle(Parkable vehicle) {
        this.parkedVehicle = vehicle;
    }

    // ─── toString ────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Garage #" + number + " [EMPTY]";
        }
        return "Garage #" + number + " [OCCUPIED by: " + ((Vehicle) parkedVehicle).getName() + "]";
    }
}
