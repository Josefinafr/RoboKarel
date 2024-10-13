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

    private static final int INITIAL_DELAY_MS = 1000; // Reduziere initiale Verzögerung
    private static final int COMMAND_DELAY_FORWARD_MS = 3000; // Kürzere Verzögerung für forward
    private static final int COMMAND_DELAY_LEFT_RIGHT_MS = 1000; // Kürzere Verzögerung für left und right
    private static final int COMMAND_DELAY_LOOP_MS = 500; // Neue Konstante für kürzere Pause zwischen Loops
    private static final int RETURN_DELAY_MS = 100;
    private static final float LIGHT_THRESHOLD = 10;

    private View leftField;
    private View rightField;
    private ImageView faceImage;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isFrontClear = true;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable commandRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        leftField = findViewById(R.id.leftField);
        rightField = findViewById(R.id.rightField);
        faceImage = findViewById(R.id.faceImage);

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

        // Erkenne das Wort "loop" im Code und setze ifLoop auf true, falls gefunden
        if (code != null && code.toLowerCase().contains("loop")) {
            ifLoop = true; // Loop-Modus aktivieren, wenn "loop" im Code vorkommt
        }

        final boolean finalIfLoop = ifLoop;

        if (code != null) {
            handler.postDelayed(() -> executeCommands(code.split("\n"), finalIfLoop), INITIAL_DELAY_MS);
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show();
        }
    }

    private final SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lightLevel = event.values[0];
                isFrontClear = lightLevel > LIGHT_THRESHOLD;

                // Sofortiger Abbruch, wenn "Front Is Clear" nicht mehr wahr ist
                if (!isFrontClear) {
                    returnToCodeScreenImmediately();  // Bricht sofort ab
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
                // Prüfen, ob die Schleife abgebrochen werden soll
                if (!isFrontClear) {
                    returnToCodeScreenImmediately();
                    return;
                }

                if (index < commands.length) {
                    String command = commands[index].trim();

                    // "loop"-Befehl ignorieren und stattdessen den Loop-Modus aktivieren
                    if (command.equals("loop")) {
                        index++; // Überspringe den "loop"-Befehl und setze den Loop fort
                        handler.post(this);
                        return;
                    }

                    if (!command.isEmpty()) {
                        executeCommand(command);
                    }

                    int delay = COMMAND_DELAY_FORWARD_MS;
                    if (command.equals("left") || command.equals("right")) {
                        delay = COMMAND_DELAY_LEFT_RIGHT_MS;
                    }

                    index++;
                    handler.postDelayed(this, delay);
                } else if (ifLoop && isFrontClear) {
                    // Setze den Index zurück und starte erneut, wenn in der Schleife und „Front Is Clear“
                    index = 0;
                    handler.postDelayed(this, COMMAND_DELAY_LOOP_MS); // Verkürzte Pause zwischen den Loops
                } else {
                    // Nach Abschluss des normalen Ablaufs ohne Schleife oder wenn Loop abgebrochen wird
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
        handler.removeCallbacks(commandRunnable);  // Entfernt alle geplanten Befehle
        finish();  // Beende die aktuelle Aktivität
    }

    private void returnToCodeScreen() {
        handler.postDelayed(() -> finish(), RETURN_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lightSensor != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }
        handler.removeCallbacks(commandRunnable);  // Alle geplanten Befehle entfernen, wenn die Aktivität zerstört wird
    }
}

