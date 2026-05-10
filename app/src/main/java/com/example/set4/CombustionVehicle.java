package com.example.set4;

/**
 * Interface for vehicles powered by combustion engines.
 *
 * Fuel type bitmask constants:
 *   DIESEL = 1 (bit 0)
 *   PETROL = 2 (bit 1)
 *   LPG    = 4 (bit 2)
 *   CNG    = 8 (bit 3)
 *
 * A vehicle may support multiple fuels simultaneously using bitwise OR,
 * e.g., PETROL | LPG = 6.
 *
 * XML fuelType integer mapping:
 *   0 -> no fuel (invalid for combustion, treat as PETROL fallback)
 *   1 -> DIESEL
 *   2 -> PETROL
 *   3 -> DIESEL | PETROL
 *   4 -> LPG
 *   5 -> DIESEL | LPG
 *   6 -> PETROL | LPG
 *   7 -> DIESEL | PETROL | LPG
 *   8 -> CNG
 *   ... (any bitwise combination of the four fuel bits)
 *
 * The integer stored in XML IS the bitmask directly.
 */
public interface CombustionVehicle {

    // Fuel type bitmask constants
    int DIESEL = 1 << 0; // = 1
    int PETROL = 1 << 1; // = 2
    int LPG    = 1 << 2; // = 4
    int CNG    = 1 << 3; // = 8

    /**
     * Attempts to add fuel to the vehicle.
     *
     * @param fuelMask bitmask of the fuel type being added
     * @param liters   amount of fuel in liters (must be > 0)
     * @return true  if the fuel type is supported and liters > 0 (fuel added),
     *         false if the fuel type is unsupported OR liters <= 0 (no change)
     */
    boolean refuel(int fuelMask, double liters);

    /**
     * Returns the bitmask of fuel types this vehicle supports.
     */
    int getSupportedFuelMask();

    /**
     * Returns the current total fuel amount in liters.
     */
    double getFuelAmount();

    /**
     * Returns a human-readable string of supported fuel types based on a bitmask.
     */
    static String fuelMaskToString(int mask) {
        if (mask == 0) return "NONE";
        StringBuilder sb = new StringBuilder();
        if ((mask & DIESEL) != 0) { if (sb.length() > 0) sb.append("|"); sb.append("DIESEL"); }
        if ((mask & PETROL) != 0) { if (sb.length() > 0) sb.append("|"); sb.append("PETROL"); }
        if ((mask & LPG)    != 0) { if (sb.length() > 0) sb.append("|"); sb.append("LPG"); }
        if ((mask & CNG)    != 0) { if (sb.length() > 0) sb.append("|"); sb.append("CNG"); }
        return sb.toString();
    }
}
