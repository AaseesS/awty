package edu.uw.ischool.asingh28.awty

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextInterval: EditText
    private lateinit var buttonStartStop: Button

    private var isServiceRunning = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextMessage = findViewById(R.id.editTextMessage)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextInterval = findViewById(R.id.editTextInterval)
        buttonStartStop = findViewById(R.id.buttonStartStop)

        buttonStartStop.setOnClickListener {
            if (!isServiceRunning) {
                if (validateInput()) {
                    startSendingMessages()
                }
            } else {
                stopSendingMessages()
            }
        }
    }

    private fun validateInput(): Boolean {
        val message = editTextMessage.text.toString()
        val intervalStr = editTextInterval.text.toString()

        if (message.isEmpty() || intervalStr.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        val interval = intervalStr.toInt()
        if (interval <= 0) {
            Toast.makeText(this, "Interval must be a positive integer and bigger than 0", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun startSendingMessages() {
        buttonStartStop.text = "Stop"
        isServiceRunning = true
        val message = editTextMessage.text.toString()
        val interval = editTextInterval.text.toString().toLong() * 60 * 1000

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                val phoneNumber = editTextPhoneNumber.text.toString()
                val formattedMessage = "($phoneNumber): $message"
                Toast.makeText(this@MainActivity, formattedMessage, Toast.LENGTH_SHORT).show()

                handler?.postDelayed(this, interval)
            }
        }
        handler?.post(runnable!!)
    }

    private fun stopSendingMessages() {
        buttonStartStop.text = "Start"
        isServiceRunning = false
        handler?.removeCallbacks(runnable!!)
    }
}
