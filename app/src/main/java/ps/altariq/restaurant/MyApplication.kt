package ps.altariq.restaurant

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import ps.altariq.restaurant.di.component.DaggerApplicationComponent
import ps.altariq.restaurant.ui.common.helper.LocaleManager
import ps.altariq.restaurant.ui.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

class MyApplication : DaggerApplication() {
    private val applicationInjector = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    @Inject
    protected lateinit var fabric: Fabric

    @Inject
    protected lateinit var timberTree: Timber.Tree

    lateinit var localeManager: LocaleManager

    protected lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        Timber.plant(timberTree)

        //Thread.sleep(1000)
    }

    override fun applicationInjector() = applicationInjector

//    override fun attachBaseContext(base: Context) {
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(base)
//
////        localeManager = LocaleManager()
////        super.attachBaseContext(localeManager.updateResources(base, sharedPreferences.getString(
////            LocaleManager.LANGUAGE_KEY,
////            Locale.getDefault().language
////        )))
//        Timber.d("attachBaseContext")
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        localeManager.updateResources(this, sharedPreferences.getString(
//            LocaleManager.LANGUAGE_KEY,
//            Locale.getDefault().language
//        ))
//        Timber.d("onConfigurationChanged: " + newConfig.locale.language)
//    }

    override fun onLowMemory() {
        super.onLowMemory()

        Runtime.getRuntime().gc()

        val intent = Intent(applicationContext, MainActivity::class.java)
        val mPendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }
}