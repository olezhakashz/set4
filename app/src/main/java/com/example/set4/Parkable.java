package com.example.set4;

/**
 * Interface for vehicles that can be parked in a Garage.
 *
 * Rules:
 * - park(garage) returns true only if the garage is empty AND the vehicle
 *   is not already parked elsewhere; both references are updated atomically.
 * - park(garage) returns false if the garage is occupied OR vehicle is already parked.
 * - unpark() returns true only if the vehicle was parked and both the vehicle
 *   and garage references are cleared; returns false if vehicle was not parked.
 */
public interface Parkable {

    /**
     * Parks this vehicle in the given garage.
     *
     * @param garage the target garage
     * @return true if successfully parked, false otherwise
     */
    boolean park(Garage garage);

    /**
     * Removes this vehicle from its current garage.
     *
     * @return true if the vehicle was parked and is now freed, false otherwise
     */
    boolean unpark();

    /**
     * Returns whether this vehicle is currently parked in a garage.
     */
    boolean isParked();

    /**
     * Returns the garage this vehicle is parked in, or null if not parked.
     */
    Garage getGarage();
}
