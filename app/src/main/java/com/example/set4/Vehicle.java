package com.example.set4;

/**
 * Abstract base class for all vehicles in the rental system.
 *
 * ID assignment:
 * - nextId is a static counter that auto-increments for every new vehicle.
 * - The constructor always assigns id = nextId++ ensuring no duplicates at runtime.
 * - When loading from XML, IDs are still assigned via this constructor
 *   (order of loading determines IDs).
 */
public abstract class Vehicle implements Comparable<Vehicle> {

    /** Global counter used to generate unique IDs for every vehicle instance. */
    private static int nextId = 1;

    private final int id;
    private String name;

    /**
     * Constructs a vehicle with the given name.
     * Automatically assigns a unique ID using nextId.
     *
     * @param name the vehicle name
     */
    public Vehicle(String name) {
        this.id   = nextId++;
        this.name = name;
    }

    // ─── Required API ────────────────────────────────────────────────────────

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract String toString();

    // ─── Package-level helper used only by Rental when resetting state ────────

    /**
     * Resets the global ID counter (used when re-loading data in tests or if
     * you need to restart numbering from 1).
     * Not required by the spec but useful for deterministic XML reload tests.
     */
    static void resetNextId() {
        nextId = 1;
    }

    /**
     * Returns the current value of nextId without incrementing it.
     * Used by Rental to determine the next expected ID after XML load.
     */
    static int peekNextId() {
        return nextId;
    }

    // ─── Sort key helpers (used by Comparator in Rental) ─────────────────────

    /**
     * Returns a numeric type-order value for sorting:
     *   Car=0, Motorboat=1, Bicycle=2, Scooter=3
     */
    public int typeOrder() {
        if (this instanceof Car)       return 0;
        if (this instanceof Motorboat) return 1;
        if (this instanceof Bicycle)   return 2;
        if (this instanceof Scooter)   return 3;
        return Integer.MAX_VALUE;
    }

    /**
     * Returns 0 if parked, 1 if not parked.
     * Non-parkable vehicles are always treated as "not parked" (1).
     * Sorting: parked vehicles come FIRST.
     */
    public int parkOrder() {
        if (this instanceof Parkable) {
            return ((Parkable) this).isParked() ? 0 : 1;
        }
        return 1;
    }

    /**
     * Returns supported fuel bitmask, or 0 for non-combustion vehicles.
     */
    public int fuelMaskOrder() {
        if (this instanceof CombustionVehicle) {
            return ((CombustionVehicle) this).getSupportedFuelMask();
        }
        return 0;
    }

    /**
     * Returns fuel amount, or 0.0 for non-combustion vehicles.
     */
    public double fuelAmountOrder() {
        if (this instanceof CombustionVehicle) {
            return ((CombustionVehicle) this).getFuelAmount();
        }
        return 0.0;
    }

    // ─── Comparable: multi-criteria sort ─────────────────────────────────────
    /**
     * Sort order (ascending):
     *  1. Parked first (0 = parked, 1 = not parked)
     *  2. Vehicle type: Car < Motorboat < Bicycle < Scooter
     *  3. Name ascending (case-insensitive)
     *  4. Fuel type (bitmask) ascending
     *  5. Fuel amount ascending
     */
    @Override
    public int compareTo(Vehicle other) {
        // 1. Parked first
        int cmp = Integer.compare(this.parkOrder(), other.parkOrder());
        if (cmp != 0) return cmp;

        // 2. Type order
        cmp = Integer.compare(this.typeOrder(), other.typeOrder());
        if (cmp != 0) return cmp;

        // 3. Name ascending
        cmp = this.getName().compareToIgnoreCase(other.getName());
        if (cmp != 0) return cmp;

        // 4. Fuel type (mask) ascending
        cmp = Integer.compare(this.fuelMaskOrder(), other.fuelMaskOrder());
        if (cmp != 0) return cmp;

        // 5. Fuel amount ascending
        return Double.compare(this.fuelAmountOrder(), other.fuelAmountOrder());
    }
}
