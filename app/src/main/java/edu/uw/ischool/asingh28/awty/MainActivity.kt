package edu.uw.ischool.asingh28.awty

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
            Toast.makeText(this, "Interval must be a positive number and bigger than 0", Toast.LENGTH_SHORT).show()
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
    private fun sendSms(phoneNumber: String, message: String) {
        try {
            if (checkSmsPermission()) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            } else {
                requestSmsPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@MainActivity,
                "Failed to send SMS. Please check your settings.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun checkSmsPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
    }
    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 123
    }
}
