package com.example.robokarel;

import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        leftField = findViewById(R.id.leftField);
        rightField = findViewById(R.id.rightField);
        faceImage = findViewById(R.id.faceImage);

        // Stelle sicher, dass das Bild korrekt geladen wird
        faceImage.setImageResource(R.drawable.face);

        String code = getIntent().getStringExtra("code");
        boolean ifLoop = getIntent().getBooleanExtra("ifLoop", false);

        if (code != null) {
            // Verzögerung von 8 Sekunden, bevor die Befehle ausgeführt werden
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

    private void executeCommands(String[] commands, boolean ifLoop) {
        // Logik, um die Befehle auszuführen und die Felder zu steuern
        for (String command : commands) {
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

            try {
                Thread.sleep(5000); // Pause für 5 Sekunden
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Verzögerung von 10 Sekunden, bevor zum Code-Bildschirm zurückgekehrt wird
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                finish(); // Beendet die aktuelle Aktivität und kehrt zum vorherigen Bildschirm zurück
            }
        }, 10000); // 10000 Millisekunden = 10 Sekunden
    }
}
