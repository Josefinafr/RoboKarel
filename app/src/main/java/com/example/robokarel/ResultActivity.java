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

import com.example.robokarel.R;

public class ResultActivity extends AppCompatActivity {

    private View leftField;
    private View rightField;
    private ImageView faceImage;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean isFrontClear = true;

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
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor != null) {
            sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        String code = getIntent().getStringExtra("code");
        boolean ifLoop = getIntent().getBooleanExtra("ifLoop", false);

        if (code != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    executeCommands(code.split("\n"), ifLoop);
                }
            }, 8000);
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show();
        }
    }

    private final SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                isFrontClear = event.values[0] > 0; // assumes 0 means close, >0 means far
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing for now
        }
    };

    private void executeCommands(String[] commands, boolean ifLoop) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable commandRunnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < commands.length) {
                    String command = commands[index];
                    executeCommand(command);
                    index++;
                    handler.postDelayed(this, 5000); // Nächster Befehl nach 5 Sekunden
                } else if (ifLoop && isFrontClear) {
                    // Reset index und starte erneut, wenn in Schleife und Front clear
                    index = 0;
                    handler.postDelayed(this, 5000); // Nächster Befehl nach 5 Sekunden
                } else if (ifLoop && !isFrontClear) {
                    // Wenn in Schleife und Hindernis, warte 1 Sekunde und prüfe erneut
                    handler.postDelayed(this, 1000);
                } else {
                    // Nach Abschluss oder wenn Loop abgebrochen wird
                    handler.postDelayed(() -> finish(), 10000); // Zurück zum Code-Bildschirm nach 10 Sekunden
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximitySensor != null) {
            sensorManager.unregisterListener(proximitySensorListener);
        }
    }
}
