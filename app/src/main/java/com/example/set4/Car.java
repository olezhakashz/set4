package com.example.set4;

/**
 * A car: extends Vehicle, implements CombustionVehicle and Parkable.
 *
 * Fuel is tracked as a running total (liters). Multiple fuel types may be
 * supported simultaneously via bitmask.
 */
public class Car extends Vehicle implements CombustionVehicle, Parkable {

    /** Bitmask of supported fuel types (DIESEL, PETROL, LPG, CNG constants). */
    private final int supportedFuelMask;

    /** Current fuel in the tank (liters). */
    private double fuelAmount;

    /** Reference to the garage where this car is parked, or null. */
    private Garage garage;

    /**
     * Constructs a Car.
     *
     * @param name            vehicle name
     * @param supportedFuelMask bitmask of supported fuels (e.g. PETROL | LPG)
     */
    public Car(String name, int supportedFuelMask) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
        this.fuelAmount        = 0.0;
        this.garage            = null;
    }

    // ─── CombustionVehicle ───────────────────────────────────────────────────

    /**
     * Adds fuel if the given fuelMask is supported and liters > 0.
     *
     * @return true if fuel added, false if unsupported fuel type or liters <= 0
     */
    @Override
    public boolean refuel(int fuelMask, double liters) {
        if (liters <= 0) return false;
        if ((supportedFuelMask & fuelMask) == 0) return false;
        fuelAmount += liters;
        return true;
    }

    @Override
    public int getSupportedFuelMask() {
        return supportedFuelMask;
    }

    @Override
    public double getFuelAmount() {
        return fuelAmount;
    }

    // ─── Parkable ────────────────────────────────────────────────────────────

    /**
     * Parks this car in the given garage.
     * Succeeds only if both:
     *  - the garage is empty, and
     *  - this car is not already parked elsewhere.
     */
    @Override
    public boolean park(Garage garage) {
        if (garage == null)       return false;
        if (!garage.isEmpty())    return false;
        if (this.garage != null)  return false;

        this.garage = garage;
        garage.setParkedVehicle(this);
        return true;
    }

    /**
     * Removes this car from its current garage.
     * Returns false if the car was not parked.
     */
    @Override
    public boolean unpark() {
        if (garage == null) return false;
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
        String fuels   = CombustionVehicle.fuelMaskToString(supportedFuelMask);
        String parked  = isParked()
                ? "Parked in Garage #" + garage.getNumber()
                : "Not parked";
        return String.format("[%d] Car | %s | Fuels: %s | Fuel: %.1f L | %s",
                getId(), getName(), fuels, fuelAmount, parked);
    }
}
