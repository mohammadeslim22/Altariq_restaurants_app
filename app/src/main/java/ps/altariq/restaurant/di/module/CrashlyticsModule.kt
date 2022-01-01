package ps.altariq.restaurant.di.module

import android.content.Context
import com.crashlytics.android.BuildConfig
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import ps.altariq.restaurant.di.qualifier.ApplicationContext
import dagger.Module
import dagger.Provides
import io.fabric.sdk.android.Fabric
import javax.inject.Singleton

/**
 * https://firebase.google.com/docs/crashlytics/
 */
@Module
object CrashlyticsModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideCrashlyticsCore(): CrashlyticsCore =
        CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideCrashlytics(crashlyticsCore: CrashlyticsCore): Crashlytics =
        Crashlytics.Builder().core(crashlyticsCore).build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideFabric(@ApplicationContext context: Context, crashlytics: Crashlytics): Fabric =
        Fabric.with(context, crashlytics)
}