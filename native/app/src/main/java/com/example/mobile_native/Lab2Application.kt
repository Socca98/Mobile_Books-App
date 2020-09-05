package com.example.mobile_native

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.mobile_native.util.ThemeManager
import io.reactivex.Observable
import io.realm.Realm

/*
- Tutorials that helped -
RxJava - very good video
https://youtu.be/YPf6AYDaYf8

Settings/Preference
https://www.youtube.com/watch?v=-F29CMk48RA

Dark mode
https://www.raywenderlich.com/6488033-android-10-dark-theme-getting-started

In order to initialize only once the Realm,
we create this entry point class.
From XML manifest
android:name=".MyApplication"  this tells it to start in this class, not in MainActivity
 */

class Lab2Application : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Realm. Should only be done once
        // when the application starts.
        Realm.init(this)
        initTheme()

//        Observable.just(1, 2,3 )

    }

    /**
     * Persistent dark theme. Resists app restarts.
     */
    private fun initTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePreferenceBoolean = preferences.getBoolean("preference_key_theme", false)
        var themePreference = "Light"
        if (themePreferenceBoolean)
            themePreference = "Dark"
        ThemeManager.applyTheme(themePreference)
    }
}

/*
*/




