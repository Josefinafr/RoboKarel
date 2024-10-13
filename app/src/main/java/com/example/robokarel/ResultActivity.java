package com.example.robokarel;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    // Verzögerungen für verschiedene Befehle und Schleifen
    private static final int INITIAL_DELAY_MS = 1000; // Verzögerung vor dem Start der Befehlsausführung
    private static final int COMMAND_DELAY_FORWARD_MS = 2000; // Verzögerung für "forward"-Befehl
    private static final int COMMAND_DELAY_LEFT_RIGHT_MS = 500; // Verzögerung für "left" und "right"-Befehle
    private static final int COMMAND_DELAY_LOOP_MS = 500; // Verzögerung zwischen den Loop-Wiederholungen
    private static final int RETURN_DELAY_MS = 100; // Verzögerung vor dem Rücksprung zum Code-Bildschirm
    private static final float LIGHT_THRESHOLD = 10; // Schwelle für die Lichtintensität zur Prüfung, ob „Front Is Clear“

    private View leftField;
    private View rightField;
    private ImageView faceImage;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isFrontClear = true; // Standardmäßig ist „Front Is Clear“ auf true gesetzt
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler zur Ausführung von verzögerten Aufgaben
    private Runnable commandRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialisiere Views und setze das Bild für das "faceImage"
        leftField = findViewById(R.id.leftField);
        rightField = findViewById(R.id.rightField);
        faceImage = findViewById(R.id.faceImage);
        faceImage.setImageResource(R.drawable.face); // Lade das Bild für das Gesicht

        // Initialisiere den Lichtsensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Falls ein Lichtsensor verfügbar ist, registriere den Listener
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Lichtsensor nicht verfügbar", Toast.LENGTH_SHORT).show();
        }

        // Hole den Code und den Loop-Modus aus der übergebenen Absicht (Intent)
        String code = getIntent().getStringExtra("code");
        boolean ifLoop = getIntent().getBooleanExtra("ifLoop", false);

        // Überprüfe, ob der Text "loop" im Code vorhanden ist und aktiviere den Loop-Modus, falls er gefunden wird
        if (code != null && code.toLowerCase().contains("loop")) {
            ifLoop = true; // Setze Loop-Modus auf true, wenn "loop" im Code enthalten ist
        }

        final boolean finalIfLoop = ifLoop; // Macht ifLoop final für die Lambda-Ausdrücke

        // Starte die Befehle nach der initialen Verzögerung, falls ein Code bereitgestellt wurde
        if (code != null) {
            handler.postDelayed(() -> executeCommands(code.split("\n"), finalIfLoop), INITIAL_DELAY_MS);
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show();
        }
    }

    // SensorEventListener für den Lichtsensor
    private final SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                // Hole den aktuellen Lichtlevel in Lux
                float lightLevel = event.values[0];
                isFrontClear = lightLevel > LIGHT_THRESHOLD; // Setze isFrontClear basierend auf dem Lichtlevel

                // Wenn "Front Is Clear" nicht mehr wahr ist, breche den Code sofort ab
                if (!isFrontClear) {
                    returnToCodeScreenImmediately();  // Bricht sofort ab und kehrt zum Code-Bildschirm zurück
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Keine Aktion erforderlich bei Änderungen der Genauigkeit
        }
    };

    // Führt die Befehle im Code aus
    private void executeCommands(String[] commands, boolean ifLoop) {
        commandRunnable = new Runnable() {
            int index = 0; // Index für den aktuellen Befehl

            @Override
            public void run() {
                // Falls "Front Is Clear" nicht mehr wahr ist, sofort abbrechen
                if (!isFrontClear) {
                    returnToCodeScreenImmediately();
                    return;
                }

                // Führe die Befehle der Reihe nach aus
                if (index < commands.length) {
                    String command = commands[index].trim();

                    // Ignoriere den "loop"-Befehl im Text und setze den Loop-Modus fort
                    if (command.equals("loop")) {
                        index++; // Überspringe den "loop"-Befehl und gehe zum nächsten Befehl
                        handler.post(this); // Setze die Schleife fort
                        return;
                    }

                    // Führe den aktuellen Befehl aus
                    if (!command.isEmpty()) {
                        executeCommand(command); // Führe den Befehl aus (forward, left, right, stop)
                    }

                    // Verzögerung anpassen basierend auf dem Befehl
                    int delay = COMMAND_DELAY_FORWARD_MS; // Standardverzögerung für "forward"
                    if (command.equals("left") || command.equals("right")) {
                        delay = COMMAND_DELAY_LEFT_RIGHT_MS; // Kürzere Verzögerung für "left" und "right"
                    }

                    // Zum nächsten Befehl übergehen nach der Verzögerung
                    index++;
                    handler.postDelayed(this, delay);
                } else if (ifLoop && isFrontClear) {
                    // Wenn im Loop-Modus und "Front Is Clear", setze den Index zurück und starte von vorne
                    index = 0;
                    handler.postDelayed(this, COMMAND_DELAY_LOOP_MS); // Verkürzte Verzögerung für Loop-Wiederholung
                } else {
                    // Wenn keine Befehle mehr auszuführen sind, kehre zum Code-Bildschirm zurück
                    returnToCodeScreen();
                }
            }
        };
        handler.post(commandRunnable); // Starte die Befehlsausführung
    }

    // Führe den jeweiligen Befehl aus, indem das Farbmuster der Felder geändert wird
    private void executeCommand(String command) {
        switch (command) {
            case "forward":
                leftField.setBackgroundColor(Color.WHITE);
                rightField.setBackgroundColor(Color.WHITE);
                break;
            case "left":
                leftField.setBackgroundColor(Color.WHITE);
                rightField.setBackgroundColor(Color.BLACK);
                break;
            case "right":
                leftField.setBackgroundColor(Color.BLACK);
                rightField.setBackgroundColor(Color.WHITE);
                break;
            case "stop":
                leftField.setBackgroundColor(Color.BLACK);
                rightField.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    // Bricht den Code sofort ab und kehrt zum Code-Bildschirm zurück
    private void returnToCodeScreenImmediately() {
        handler.removeCallbacks(commandRunnable);  // Entfernt alle geplanten Befehle
        finish();  // Beende die aktuelle Aktivität
    }

    // Kehrt nach einer kleinen Verzögerung zum Code-Bildschirm zurück
    private void returnToCodeScreen() {
        handler.postDelayed(() -> finish(), RETURN_DELAY_MS); // Beende die Aktivität nach RETURN_DELAY_MS
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Deregistriere den Lichtsensor-Listener und entferne alle geplanten Befehle
        if (lightSensor != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }
        handler.removeCallbacks(commandRunnable);  // Entferne alle geplanten Befehle beim Zerstören der Aktivität
    }
}
