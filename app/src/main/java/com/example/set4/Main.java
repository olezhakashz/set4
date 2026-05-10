package com.example.set4;

/**
 * Plain Java entry point for the Vehicle Rental System.
 *
 * How to run (from the project root):
 *
 *   1. Compile all sources:
 *        javac -d out app\src\main\java\com\example\set4\*.java
 *
 *   2. Copy vehicles.xml next to the working directory:
 *        copy app\src\main\assets\vehicles.xml out\vehicles.xml
 *
 *   3. Run:
 *        java -cp out com.example.set4.Main
 *
 * Or use Android Studio's built-in terminal (Alt+F12) and run the
 * commands above from the project root folder.
 */
public class Main {

    public static void main(String[] args) {
        // Resolve paths relative to where the JVM is launched from
        String inputXml  = "app/src/main/assets/vehicles.xml";
        String outputXml = "vehicles_saved.xml";

        // Allow override via command-line args: Main <inputXml> <outputXml>
        if (args.length >= 1) inputXml  = args[0];
        if (args.length >= 2) outputXml = args[1];

        // 5 garages as required by the specification
        Rental rental = new Rental(5, inputXml, outputXml);
        rental.loadFromXml();
        rental.runMenu();
    }
}
