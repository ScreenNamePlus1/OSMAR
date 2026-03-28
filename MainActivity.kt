package com.yourcompany.arnav

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.yourcompany.arnav.databinding.ActivityMainBinding
import com.yourcompany.arnav.navigation.RoutingEngine
import com.yourcompany.arnav.voice.NavigationCommand
import com.yourcompany.arnav.voice.VoiceCommandProcessor
import com.yourcompany.arnav.voice.VoskRecognizer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var routingEngine: RoutingEngine
    private lateinit var voskRecognizer: VoskRecognizer
    private lateinit var commandProcessor: VoiceCommandProcessor
    
    private val PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )
    
    private val PERMISSION_REQUEST_CODE = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        checkPermissions()
    }
    
    private fun checkPermissions() {
        val missingPermissions = PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isEmpty()) {
            initializeApp()
        } else {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeApp()
            } else {
                Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    
    private fun initializeApp() {
        routingEngine = RoutingEngine(this)
        
        lifecycleScope.launch {
            val initialized = routingEngine.initialize()
            if (!initialized) {
                Toast.makeText(
                    this@MainActivity,
                    "Please download map data in settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        val modelPath = "${filesDir}/vosk-model"
        voskRecognizer = VoskRecognizer(this, modelPath)
        commandProcessor = VoiceCommandProcessor()
        
        setupUI()
        startVoiceControl()
    }
    
    private fun setupUI() {
        binding.apply {
            btnARMode.setOnClickListener {
                startActivity(ARNavigationActivity.newIntent(this@MainActivity))
            }
            
            btnMapMode.setOnClickListener {
                Toast.makeText(this@MainActivity, "Map mode coming soon", Toast.LENGTH_SHORT).show()
            }
            
            btnSettings.setOnClickListener {
                Toast.makeText(this@MainActivity, "Settings coming soon", Toast.LENGTH_SHORT).show()
            }
            
            btnVoice.setOnClickListener {
                Toast.makeText(this@MainActivity, "Voice active", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startVoiceControl() {
        voskRecognizer.startListening { transcript ->
            runOnUiThread {
                handleVoiceInput(transcript)
            }
        }
    }
    
    private fun handleVoiceInput(transcript: String) {
        binding.tvStatus.text = "Heard: $transcript"
        
        val command = commandProcessor.parseCommand(transcript)
        
        when (command) {
            is NavigationCommand.NavigateTo -> {
                binding.tvStatus.text = "Navigating to: ${command.destination}"
            }
            
            is NavigationCommand.FindPOI -> {
                binding.tvStatus.text = "Finding: ${command.type}"
            }
            
            NavigationCommand.SwitchToAR -> {
                startActivity(ARNavigationActivity.newIntent(this))
            }
            
            else -> {
                // Unknown command
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        voskRecognizer.release()
        routingEngine.shutdown()
    }
}
