import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var leftField: View
    private lateinit var rightField: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        leftField = findViewById(R.id.leftField)
        rightField = findViewById(R.id.rightField)

        val code = intent.getStringExtra("code")
        val ifLoop = intent.getBooleanExtra("ifLoop", false)

        if (code != null) {
            // Erstelle einen Handler und füge eine Verzögerung von 8 Sekunden hinzu
            Handler(Looper.getMainLooper()).postDelayed({
                executeCommands(code.split("\n"), ifLoop)
            }, 8000) // 8000 Millisekunden = 8 Sekunden
        } else {
            Toast.makeText(this, "No code provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeCommands(commands: List<String>, ifLoop: Boolean) {
        // Implementiere die Logik, um die Befehle auszuführen und die Felder zu steuern
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
    }
}
