package com.example.set4;

/**
 * A bicycle: extends Vehicle, implements Parkable.
 * No combustion engine — no fuel tracking.
 */
public class Bicycle extends Vehicle implements Parkable {

    /** Reference to the garage where this bicycle is parked, or null. */
    private Garage garage;

    /**
     * Constructs a Bicycle.
     *
     * @param name vehicle name
     */
    public Bicycle(String name) {
        super(name);
        this.garage = null;
    }

    // ─── Parkable ────────────────────────────────────────────────────────────

    /**
     * Parks this bicycle in the given garage.
     * Succeeds only if:
     * - the garage is empty, and
     * - this bicycle is not already parked elsewhere.
     */
    @Override
    public boolean park(Garage garage) {
        if (garage == null)
            return false;
        if (!garage.isEmpty())
            return false;
        if (this.garage != null)
            return false;

        this.garage = garage;
        garage.setParkedVehicle(this);
        return true;
    }

    /**
     * Removes this bicycle from its current garage.
     * Returns false if the bicycle was not parked.
     */
    @Override
    public boolean unpark() {
        if (garage == null)
            return false;
        garage.setParkedVehicle(null);
        garage = null;
        return true;
    }

    @Override
    public boolean isParked() {
        return garage != null;
    }

    @Override
    public Garage getGarage() {
        return garage;
    }

    // ─── toString ────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        String parked = isParked()
                ? "Parked in Garage #" + garage.getNumber()
                : "Not parked";
        return String.format("[%d] Bicycle | %s | %s",
                getId(), getName(), parked);
    }
}
