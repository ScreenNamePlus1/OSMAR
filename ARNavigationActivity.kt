package com.yourcompany.arnav

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ARNavigationActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_navigation)
    }
    
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ARNavigationActivity::class.java)
        }
    }
}
