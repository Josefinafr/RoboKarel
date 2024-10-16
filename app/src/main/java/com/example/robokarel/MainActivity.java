package com.example.robokarel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity
 *
 * The MainActivity.java file is the primary interface of the application,
 * where users can input commands manually or add them using predefined buttons
 *
 *
 * @author Josefina Fritz
 */
public class MainActivity extends AppCompatActivity {

    private EditText codeInput;
    private CheckBox ifLoopCheckbox;
    private Button forwardButton;
    private Button leftButton;
    private Button rightButton;
    private Button stopButton;
    private Button playButton;
    private Button loopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisierung der Views
        codeInput = findViewById(R.id.codeInput);
        ifLoopCheckbox = findViewById(R.id.ifLoopCheckbox);
        forwardButton = findViewById(R.id.forwardButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        stopButton = findViewById(R.id.stopButton);
        playButton = findViewById(R.id.playButton);
        loopButton = findViewById(R.id.loopButton); /

        // Hinzufügen der OnClickListener zu den Buttons
        forwardButton.setOnClickListener(v -> codeInput.append("forward\n"));
        leftButton.setOnClickListener(v -> codeInput.append("left\n"));
        rightButton.setOnClickListener(v -> codeInput.append("right\n"));
        stopButton.setOnClickListener(v -> codeInput.append("stop\n"));

        // OnClickListener für den Loop-Button
        loopButton.setOnClickListener(v -> {
            codeInput.append("loop\n");
        });

        // OnClickListener für den Play-Button
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("code", codeInput.getText().toString());
            intent.putExtra("ifLoop", ifLoopCheckbox.isChecked());
            startActivity(intent);
        });
    }
}
