package com.example.codexblockly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var codeInput: EditText
    private lateinit var ifLoopCheckbox: CheckBox
    private lateinit var forwardButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var stopButton: Button
    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeInput = findViewById(R.id.codeInput)
        ifLoopCheckbox = findViewById(R.id.ifLoopCheckbox)
        forwardButton = findViewById(R.id.forwardButton)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)
        stopButton = findViewById(R.id.stopButton)
        playButton = findViewById(R.id.playButton)

        forwardButton.setOnClickListener {
            codeInput.append("forward\n")
        }

        leftButton.setOnClickListener {
            codeInput.append("left\n")
        }

        rightButton.setOnClickListener {
            codeInput.append("right\n")
        }

        stopButton.setOnClickListener {
            codeInput.append("stop\n")
        }

        playButton.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("code", codeInput.text.toString())
            intent.putExtra("ifLoop", ifLoopCheckbox.isChecked)
            startActivity(intent)
        }
    }
}
