package com.example.set4;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Central manager for the vehicle rental system.
 *
 * Responsibilities:
 *  - Holds the ArrayList of all vehicles.
 *  - Holds the list of garages (constructed at startup).
 *  - Loads initial data from an XML file.
 *  - Saves current state to an XML file before exit.
 *  - Presents a console-style menu for all required operations.
 *
 * XML fuelType integer mapping (direct bitmask):
 *   The integer value stored in <fuelType> IS the bitmask directly:
 *     0 = no fuel   (edge case — treated as PETROL=2 fallback if 0 for combustion)
 *     1 = DIESEL
 *     2 = PETROL
 *     3 = DIESEL | PETROL
 *     4 = LPG
 *     5 = DIESEL | LPG
 *     6 = PETROL | LPG
 *     7 = DIESEL | PETROL | LPG
 *     8 = CNG
 *    (any bitwise combination of DIESEL|PETROL|LPG|CNG)
 */
public class Rental {

    // ─── Fields ──────────────────────────────────────────────────────────────

    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Garage>  garages  = new ArrayList<>();

    /** Path to the input XML file. */
    private final String inputXmlPath;

    /** Path where the output XML is saved on exit. */
    private final String outputXmlPath;

    // ─── Constructor ─────────────────────────────────────────────────────────

    /**
     * Creates a Rental with the given number of garages.
     * Uses default XML paths (vehicles.xml in the working directory).
     * This is the constructor required by the specification.
     *
     * @param garageCount number of garages to create (spec requires 5)
     */
    public Rental(int garageCount) {
        this(garageCount,
             "app/src/main/assets/vehicles.xml",
             "vehicles_saved.xml");
    }

    /**
     * Creates a Rental with the given number of garages and explicit XML file paths.
     * Used when the caller needs to override the default file locations.
     *
     * @param garageCount   number of garages to create (spec requires 5)
     * @param inputXmlPath  path to the input vehicles.xml
     * @param outputXmlPath path where the saved state will be written
     */
    public Rental(int garageCount, String inputXmlPath, String outputXmlPath) {
        this.inputXmlPath  = inputXmlPath;
        this.outputXmlPath = outputXmlPath;
        for (int i = 1; i <= garageCount; i++) {
            garages.add(new Garage(i));
        }
    }

    // ─── XML Loading ─────────────────────────────────────────────────────────

    /**
     * Loads vehicles from the input XML file.
     * Each vehicle receives a unique ID automatically via the Vehicle constructor.
     */
    public void loadFromXml() {
        try {
            InputStream is;
            File f = new File(inputXmlPath);
            if (f.exists()) {
                is = new FileInputStream(f);
            } else {
                // Fallback: try loading from classpath (useful when running from jar)
                is = getClass().getClassLoader().getResourceAsStream("vehicles.xml");
            }

            if (is == null) {
                System.out.println("[XML] vehicles.xml not found at: " + inputXmlPath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement(); // <vehicles>

            // --- Cars ---
            NodeList carNodes = root.getElementsByTagName("car");
            for (int i = 0; i < carNodes.getLength(); i++) {
                Element el   = (Element) carNodes.item(i);
                String name  = getTextContent(el, "name");
                int fuelMask = getIntContent(el, "fuelType", CombustionVehicle.PETROL);
                if (fuelMask == 0) fuelMask = CombustionVehicle.PETROL;
                vehicles.add(new Car(name, fuelMask));
            }

            // --- Motorboats ---
            NodeList boatNodes = root.getElementsByTagName("motorboat");
            for (int i = 0; i < boatNodes.getLength(); i++) {
                Element el   = (Element) boatNodes.item(i);
                String name  = getTextContent(el, "name");
                int fuelMask = getIntContent(el, "fuelType", CombustionVehicle.PETROL);
                if (fuelMask == 0) fuelMask = CombustionVehicle.PETROL;
                vehicles.add(new Motorboat(name, fuelMask));
            }

            // --- Bicycles ---
            NodeList bicycleNodes = root.getElementsByTagName("bicycle");
            for (int i = 0; i < bicycleNodes.getLength(); i++) {
                Element el  = (Element) bicycleNodes.item(i);
                String name = getTextContent(el, "name");
                vehicles.add(new Bicycle(name));
            }

            // --- Scooters ---
            NodeList scooterNodes = root.getElementsByTagName("scooter");
            for (int i = 0; i < scooterNodes.getLength(); i++) {
                Element el  = (Element) scooterNodes.item(i);
                String name = getTextContent(el, "name");
                vehicles.add(new Scooter(name));
            }

            System.out.println("[XML] Loaded " + vehicles.size() + " vehicle(s) from: " + inputXmlPath);

        } catch (Exception e) {
            System.out.println("[XML] Failed to load vehicles.xml: " + e.getMessage());
        }
    }

    private String getTextContent(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) return "";
        return nl.item(0).getTextContent().trim();
    }

    private int getIntContent(Element parent, String tag, int defaultValue) {
        String text = getTextContent(parent, tag);
        if (text.isEmpty()) return defaultValue;
        try { return Integer.parseInt(text); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    // ─── XML Saving ──────────────────────────────────────────────────────────

    /**
     * Saves all vehicles to outputXmlPath.
     * Preserves type, name, and fuelType (bitmask) for combustion vehicles.
     */
    public void saveToXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("vehicles");
            doc.appendChild(root);

            for (Vehicle v : vehicles) {
                String tag;
                if      (v instanceof Car)       tag = "car";
                else if (v instanceof Motorboat)  tag = "motorboat";
                else if (v instanceof Bicycle)    tag = "bicycle";
                else if (v instanceof Scooter)    tag = "scooter";
                else continue;

                Element el = doc.createElement(tag);

                Element nameEl = doc.createElement("name");
                nameEl.setTextContent(v.getName());
                el.appendChild(nameEl);

                if (v instanceof CombustionVehicle) {
                    int mask = ((CombustionVehicle) v).getSupportedFuelMask();
                    Element fuelEl = doc.createElement("fuelType");
                    fuelEl.setTextContent(String.valueOf(mask));
                    el.appendChild(fuelEl);
                }

                root.appendChild(el);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(new FileOutputStream(outputXmlPath)));

            System.out.println("[XML] Saved " + vehicles.size() + " vehicle(s) to: " + outputXmlPath);

        } catch (Exception e) {
            System.out.println("[XML] Failed to save: " + e.getMessage());
        }
    }

    // ─── Menu / Operations ───────────────────────────────────────────────────

    /**
     * Starts the interactive console menu loop.
     * Reads from System.in, writes to System.out.
     */
    public void runMenu() {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n+==================================+");
        System.out.println("|   VEHICLE RENTAL SYSTEM  v1.0    |");
        System.out.println("+==================================+");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": opParkVehicle(sc);   break;
                case "2": opAddVehicle(sc);    break;
                case "3": opRemoveVehicle(sc); break;
                case "4": opPrintVehicles();   break;
                case "5": opPrintGarages();    break;
                case "6": opSortAndPrint();    break;
                case "0":
                    saveToXml();
                    System.out.println("Goodbye! State saved.");
                    running = false;
                    break;
                default:
                    System.out.println("[!] Unknown option. Please try again.");
            }
        }
        sc.close();
    }

    private void printMenu() {
        System.out.println("\n----------------------------------");
        System.out.println("  1. Park a vehicle in a garage");
        System.out.println("  2. Add a new vehicle");
        System.out.println("  3. Remove a vehicle by ID");
        System.out.println("  4. Print all vehicles");
        System.out.println("  5. Print all garages");
        System.out.println("  6. Sort and print vehicles");
        System.out.println("  0. Save & Exit");
        System.out.println("----------------------------------");
        System.out.print("  Choice: ");
    }

    // ── Operation 1: Park a vehicle ──────────────────────────────────────────

    private void opParkVehicle(Scanner sc) {
        System.out.print("  Vehicle ID: ");
        int vehicleId = readInt(sc, -1);
        System.out.print("  Garage number: ");
        int garageNum = readInt(sc, -1);

        Vehicle v = findVehicleById(vehicleId);
        if (v == null) {
            System.out.println("[!] Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        if (!(v instanceof Parkable)) {
            System.out.println("[!] Vehicle is not parkable (only Car and Bicycle can be parked).");
            return;
        }

        Garage g = findGarageByNumber(garageNum);
        if (g == null) {
            System.out.println("[!] Garage #" + garageNum + " does not exist.");
            return;
        }

        Parkable p = (Parkable) v;

        if (p.isParked()) {
            System.out.println("[!] Vehicle is already parked in Garage #"
                    + p.getGarage().getNumber() + ".");
            return;
        }
        if (!g.isEmpty()) {
            Vehicle occupant = (Vehicle) g.getParkedVehicle();
            System.out.println("[!] Garage #" + garageNum + " is occupied by ["
                    + occupant.getId() + "] " + occupant.getName() + ".");
            return;
        }

        boolean ok = p.park(g);
        if (ok) {
            System.out.println("[OK] " + v.getName()
                    + " successfully parked in Garage #" + garageNum + ".");
        } else {
            System.out.println("[!] Parking failed (unexpected error).");
        }
    }

    // ── Operation 2: Add a new vehicle ───────────────────────────────────────

    private void opAddVehicle(Scanner sc) {
        System.out.println("  Select type:");
        System.out.println("    1 = Car  |  2 = Motorboat  |  3 = Bicycle  |  4 = Scooter");
        System.out.print("  Type: ");
        int type = readInt(sc, -1);

        System.out.print("  Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("[!] Name cannot be empty.");
            return;
        }

        switch (type) {
            case 1: {
                int mask   = readFuelMask(sc);
                double lit = readInitialFuel(sc);
                Car car = new Car(name, mask);
                if (lit > 0) car.refuel(mask, lit);
                vehicles.add(car);
                System.out.println("[OK] Car added: " + car);
                break;
            }
            case 2: {
                int mask   = readFuelMask(sc);
                double lit = readInitialFuel(sc);
                Motorboat mb = new Motorboat(name, mask);
                if (lit > 0) mb.refuel(mask, lit);
                vehicles.add(mb);
                System.out.println("[OK] Motorboat added: " + mb);
                break;
            }
            case 3: {
                Bicycle bic = new Bicycle(name);
                vehicles.add(bic);
                System.out.println("[OK] Bicycle added: " + bic);
                break;
            }
            case 4: {
                Scooter sco = new Scooter(name);
                vehicles.add(sco);
                System.out.println("[OK] Scooter added: " + sco);
                break;
            }
            default:
                System.out.println("[!] Invalid vehicle type.");
        }
    }

    /**
     * Prompts for a fuel bitmask.
     * Fuel bitmask mapping: DIESEL=1, PETROL=2, LPG=4, CNG=8
     * Combine with bitwise OR (i.e. add the numbers):
     *   e.g. PETROL+LPG = 6, DIESEL+PETROL = 3
     */
    private int readFuelMask(Scanner sc) {
        System.out.println("  Fuel type bitmask (DIESEL=1, PETROL=2, LPG=4, CNG=8; add to combine):");
        System.out.println("    e.g. PETROL only=2 | DIESEL+LPG=5 | PETROL+LPG=6");
        System.out.print("  fuelType: ");
        int mask = readInt(sc, CombustionVehicle.PETROL);
        if (mask <= 0) {
            System.out.println("  [!] Invalid mask, defaulting to PETROL (2).");
            mask = CombustionVehicle.PETROL;
        }
        return mask;
    }

    private double readInitialFuel(Scanner sc) {
        System.out.print("  Initial fuel in liters (0 to skip): ");
        try {
            double d = Double.parseDouble(sc.nextLine().trim());
            return d > 0 ? d : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ── Operation 3: Remove a vehicle ────────────────────────────────────────

    private void opRemoveVehicle(Scanner sc) {
        System.out.print("  Vehicle ID to remove: ");
        int id = readInt(sc, -1);

        Vehicle v = findVehicleById(id);
        if (v == null) {
            System.out.println("[!] Vehicle with ID " + id + " not found.");
            return;
        }

        if (v instanceof Parkable && ((Parkable) v).isParked()) {
            Garage g = ((Parkable) v).getGarage();
            ((Parkable) v).unpark();
            System.out.println("  [i] Vehicle was parked in Garage #"
                    + g.getNumber() + " - automatically unparked.");
        }

        vehicles.remove(v);
        System.out.println("[OK] Vehicle [" + id + "] " + v.getName() + " removed.");
    }

    // ── Operation 4: Print all vehicles ──────────────────────────────────────

    private void opPrintVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("  (no vehicles in the system)");
            return;
        }
        System.out.println("\n--- Vehicles (" + vehicles.size() + ") ---");
        for (Vehicle v : vehicles) {
            System.out.println("  " + v);
        }
    }

    // ── Operation 5: Print all garages ───────────────────────────────────────

    private void opPrintGarages() {
        System.out.println("\n--- Garages (" + garages.size() + ") ---");
        for (Garage g : garages) {
            System.out.println("  " + g);
        }
    }

    // ── Operation 6: Sort and print ──────────────────────────────────────────

    private void opSortAndPrint() {
        Collections.sort(vehicles);
        System.out.println("\n--- Sorted Vehicles (" + vehicles.size() + ") ---");
        System.out.println("   (parked first > type > name > fuel mask > fuel amount)");
        for (Vehicle v : vehicles) {
            System.out.println("  " + v);
        }
    }

    // ─── Lookup helpers ──────────────────────────────────────────────────────

    private Vehicle findVehicleById(int id) {
        for (Vehicle v : vehicles) {
            if (v.getId() == id) return v;
        }
        return null;
    }

    private Garage findGarageByNumber(int number) {
        for (Garage g : garages) {
            if (g.getNumber() == number) return g;
        }
        return null;
    }

    // ─── Input helpers ───────────────────────────────────────────────────────

    private int readInt(Scanner sc, int defaultValue) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
