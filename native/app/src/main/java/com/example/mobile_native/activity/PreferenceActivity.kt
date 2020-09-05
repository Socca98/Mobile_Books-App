package com.example.mobile_native.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_native.R
import com.example.mobile_native.fragment.SettingsFragment

class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        // Inflate SettingsFragment into FrameLayout
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_preference, SettingsFragment())
            .commit()
    }
}