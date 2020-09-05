package com.example.mobile_native.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.mobile_native.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)
    }

    /**
     * When a preference changes it state, this function is called.
     * We check if its switch_preference and change theme accordingly.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val themePreferenceKey = "preference_key_theme"
        if (key == themePreferenceKey) {
            // Get key value pair from SharedPreferences
            // ex. "switch_preference" : false
            val themePreference = findPreference<Preference>(themePreferenceKey)
            val selectedOption = sharedPreferences.getBoolean(themePreferenceKey, true)

            when (selectedOption) {
                false -> {
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    themePreference?.summary = "Light Mode"
                }

                true -> {
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    themePreference?.summary = "Dark Mode"
                }
            }
        }
    }

    private fun setTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * For some reason we need to register the listener.
     * The listener detects changes in any preference, handled in onSharedPreferenceChanged function.
     */
    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}