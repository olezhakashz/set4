# Vehicle Rental System

| Field | Details |
|---|---|
| **Course** | Introduction to Mobile Systems |
| **Lab** | L1 |
| **Student Name** | Oleh Smolinskyi |
| **Student ID** | 53209 |

## Description

A console-based vehicle rental system implemented in Java using object-oriented principles. The system manages a fleet of vehicles (cars, motorboats, bicycles, and scooters) stored in an `ArrayList`, supports parking vehicles in garages, and persists the vehicle database to XML on exit. It demonstrates abstract classes, interfaces (`CombustionVehicle`, `Parkable`), inheritance, polymorphism, fuel-type bitmask arithmetic, and multi-criteria sorting via `Collections.sort()`.

## Project Structure

```
app/src/main/java/com/example/set4/
├── Vehicle.java            # Abstract base class (ID, name, sorting)
├── Car.java                # Extends Vehicle + CombustionVehicle + Parkable
├── Motorboat.java          # Extends Vehicle + CombustionVehicle
├── Bicycle.java            # Extends Vehicle + Parkable
├── Scooter.java            # Extends Vehicle
├── Garage.java             # Single parking slot
├── Rental.java             # Menu, XML load/save, ArrayList<Vehicle>
├── CombustionVehicle.java  # Interface: refuel, getSupportedFuelMask, getFuelAmount
├── Parkable.java           # Interface: park, unpark, isParked, getGarage
└── Main.java               # Entry point (main method)

app/src/main/assets/
└── vehicles.xml            # Initial vehicle database
```

## Menu Operations

| Option | Description |
|---|---|
| `1` | Park a vehicle in a garage |
| `2` | Add a new vehicle |
| `3` | Remove a vehicle by ID |
| `4` | Print all vehicles |
| `5` | Print all garages |
| `6` | Sort and print vehicles |
| `0` | Save state to XML and exit |
