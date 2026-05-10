package com.example.set4;

/**
 * A scooter: extends Vehicle only.
 * NOT combustion (no fuel), NOT parkable (no garage).
 */
public class Scooter extends Vehicle {

    /**
     * Constructs a Scooter.
     *
     * @param name vehicle name
     */
    public Scooter(String name) {
        super(name);
    }

    // ─── toString ────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("[%d] Scooter | %s", getId(), getName());
    }
}
