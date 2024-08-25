package com.example.codexblockly

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.robokarel.R

class ResultActivity : AppCompatActivity() {

    private lateinit var leftField: View
    private lateinit var rightField: View
    private lateinit var faceImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        leftField = findViewById(R.id.leftField)
        rightField = findViewById(R.id.rightField)
        faceImage = findViewById(R.id.faceImage)

        // Stelle sicher, dass das Bild korrekt geladen wird
        faceImage.setImageResource(R.drawable.face)

        val code = intent.getStringExtra("code")
        val ifLoop = intent.getBooleanExtra("ifLoop", false)

        if (code != null) {
            // Verzögerung von 8 Sekunden, bevor die Befehle ausgeführt werden
            Handler(Looper.getMainLooper()).postDelayed({
                executeCommands(code.split("\n"), ifLoop)
            }, 8000)
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeCommands(commands: List<String>, ifLoop: Boolean) {
        // Logik, um die Befehle auszuführen und die Felder zu steuern
        for (command in commands) {
            when (command) {
                "forward" -> {
                    leftField.setBackgroundColor(Color.WHITE)
                    rightField.setBackgroundColor(Color.WHITE)
                }
                "left" -> {
                    leftField.setBackgroundColor(Color.WHITE)
                    rightField.setBackgroundColor(Color.BLACK)
                }
                "right" -> {
                    leftField.setBackgroundColor(Color.BLACK)
                    rightField.setBackgroundColor(Color.WHITE)
                }
                "stop" -> {
                    leftField.setBackgroundColor(Color.BLACK)
                    rightField.setBackgroundColor(Color.BLACK)
                }
            }
            Thread.sleep(5000) // Pause für 5 Sekunden
        }

        // Verzögerung von 10 Sekunden, bevor zum Code-Bildschirm zurückgekehrt wird
        Handler(Looper.getMainLooper()).postDelayed({
            finish() // Beendet die aktuelle Aktivität und kehrt zum vorherigen Bildschirm zurück
        }, 10000) // 10000 Millisekunden = 10 Sekunden
    }
}
