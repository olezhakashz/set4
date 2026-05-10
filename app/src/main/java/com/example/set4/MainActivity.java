package com.example.set4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Entry point of the Vehicle Rental System.
 *
 * The console menu (Scanner + System.in/out) is launched on a background
 * thread so it does not block the Android UI thread.
 * Use the Android Studio "Run > Logcat" or the Terminal's ADB shell
 * to interact with System.in/System.out when running on a device/emulator.
 *
 * NOTE: For a pure-Java (non-Android) submission, replace this class with
 * a plain class containing a static main() that creates Rental and calls
 * loadFromXml() / runMenu().
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Launch the rental system console on a background thread
        Thread rentalThread = new Thread(() -> {
            // 5 garages as required by the specification
            // On Android: copy asset to internal storage first, then use path
            String inputXml  = getFilesDir() + "/vehicles.xml";
            String outputXml = getFilesDir() + "/vehicles_saved.xml";

            // Copy assets/vehicles.xml to internal storage on first run
            try {
                java.io.InputStream is = getAssets().open("vehicles.xml");
                java.io.FileOutputStream fos = new java.io.FileOutputStream(inputXml);
                byte[] buf = new byte[1024]; int len;
                while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
                is.close(); fos.close();
            } catch (Exception ignored) {}

            Rental rental = new Rental(5, inputXml, outputXml);
            rental.loadFromXml();
            rental.runMenu();
        });
        rentalThread.setName("RentalMenuThread");
        rentalThread.setDaemon(true);
        rentalThread.start();
    }
}