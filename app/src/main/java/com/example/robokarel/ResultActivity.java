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

    private static final int INITIAL_DELAY_MS = 5000; // Initiale Verzögerung, bevor die Befehle ausgeführt werden
    private static final int COMMAND_DELAY_MS = 3000; // Verzögerung zwischen den Befehlen
    private static final int LOOP_CHECK_DELAY_MS = 1000; // Verzögerung für Schleifenüberprüfung, wenn Hindernis vorhanden ist
    private static final int RETURN_DELAY_MS = 10000; // Verzögerung, bevor zum Code-Bildschirm zurückgekehrt wird
    private static final float LIGHT_THRESHOLD = 10; // Schwelle für die Lichtintensität, um „Front Is Clear“ zu bestimmen

    private View leftField;
    private View rightField;
    private ImageView faceImage;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isFrontClear = true; // Standardmäßig ist „Front Is Clear“ wahr
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable commandRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        leftField = findViewById(R.id.leftField);
        rightField = findViewById(R.id.rightField);
        faceImage = findViewById(R.id.faceImage);

        // Stelle sicher, dass das Bild korrekt geladen wird
        faceImage.setImageResource(R.drawable.face);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Lichtsensor nicht verfügbar", Toast.LENGTH_SHORT).show();
        }

        String code = getIntent().getStringExtra("code");
        boolean ifLoop = getIntent().getBooleanExtra("ifLoop", false);

        if (code != null) {
            handler.postDelayed(() -> executeCommands(code.split("\n"), ifLoop), INITIAL_DELAY_MS);
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show();
        }
    }

    // SensorEventListener für den Lichtsensor
    private final SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lightLevel = event.values[0]; // Lichtlevel in Lux

                // Bedingung „Front Is Clear“ basierend auf Lichtlevel festlegen
                isFrontClear = lightLevel > LIGHT_THRESHOLD; // Schwellenwert für Lichtintensität

                // Wenn "Front Is Clear" nicht mehr gegeben ist, sofort zur Code-Ansicht zurückkehren
                if (!isFrontClear) {
                    returnToCodeScreenImmediately();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Keine Aktion erforderlich
        }
    };

    private void executeCommands(String[] commands, boolean ifLoop) {
        commandRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (!isFrontClear) {
                    // Wenn "Front Is Clear" nicht mehr gegeben ist, sofort abbrechen
                    returnToCodeScreenImmediately();
                    return;
                }

                if (index < commands.length) {
                    String command = commands[index];
                    executeCommand(command);
                    index++;
                    handler.postDelayed(this, COMMAND_DELAY_MS); // Nächster Befehl nach festgelegter Verzögerung
                } else if (ifLoop && isFrontClear) {
                    // Reset index und starte erneut, wenn in Schleife und Front clear
                    index = 0;
                    handler.postDelayed(this, COMMAND_DELAY_MS); // Nächster Befehl nach festgelegter Verzögerung
                } else {
                    // Nach Abschluss oder wenn Loop abgebrochen wird
                    returnToCodeScreen();
                }
            }
        };
        handler.post(commandRunnable);
    }

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

    private void returnToCodeScreenImmediately() {
        // Entferne alle geplanten Befehle und kehre sofort zum Code-Bildschirm zurück
        handler.removeCallbacks(commandRunnable);
        finish();
    }

    private void returnToCodeScreen() {
        // Beende die aktuelle Aktivität und kehre zum Code-Bildschirm zurück nach einer Verzögerung
        handler.postDelayed(() -> finish(), RETURN_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lightSensor != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }
        handler.removeCallbacks(commandRunnable); // Entferne alle geplanten Befehle, wenn die Aktivität zerstört wird
    }
}
