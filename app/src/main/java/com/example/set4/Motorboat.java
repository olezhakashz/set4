package com.example.set4;

/**
 * A motorboat: extends Vehicle, implements CombustionVehicle.
 * NOT parkable — motorboats are stored on water or open docks, not in garages.
 */
public class Motorboat extends Vehicle implements CombustionVehicle {

    /** Bitmask of supported fuel types. */
    private final int supportedFuelMask;

    /** Current fuel in the tank (liters). */
    private double fuelAmount;

    /**
     * Constructs a Motorboat.
     *
     * @param name              vehicle name
     * @param supportedFuelMask bitmask of supported fuels
     */
    public Motorboat(String name, int supportedFuelMask) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
        this.fuelAmount        = 0.0;
    }

    // ─── CombustionVehicle ───────────────────────────────────────────────────

    /**
     * Adds fuel if the given fuelMask is supported and liters > 0.
     *
     * @return true if fuel added, false if unsupported fuel or liters <= 0
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

    // ─── toString ────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        String fuels = CombustionVehicle.fuelMaskToString(supportedFuelMask);
        return String.format("[%d] Motorboat | %s | Fuels: %s | Fuel: %.1f L",
                getId(), getName(), fuels, fuelAmount);
    }
}
