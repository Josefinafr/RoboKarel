package com.example.robokarel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robokarel.R;

public class MainActivity extends AppCompatActivity {

    private EditText codeInput;
    private CheckBox ifLoopCheckbox;
    private Button forwardButton;
    private Button leftButton;
    private Button rightButton;
    private Button stopButton;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codeInput = findViewById(R.id.codeInput);
        ifLoopCheckbox = findViewById(R.id.ifLoopCheckbox);
        forwardButton = findViewById(R.id.forwardButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        stopButton = findViewById(R.id.stopButton);
        playButton = findViewById(R.id.playButton);

        forwardButton.setOnClickListener(v -> codeInput.append("forward\n"));

        leftButton.setOnClickListener(v -> codeInput.append("left\n"));

        rightButton.setOnClickListener(v -> codeInput.append("right\n"));

        stopButton.setOnClickListener(v -> codeInput.append("stop\n"));

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.robokarel.ResultActivity.class);
            intent.putExtra("code", codeInput.getText().toString());
            intent.putExtra("ifLoop", ifLoopCheckbox.isChecked());
            startActivity(intent);
        });
    }
}
