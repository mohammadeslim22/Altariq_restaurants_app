@file:Suppress("unused")

package ps.altariq.restaurant.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule
import ps.altariq.restaurant.MyApplication
import ps.altariq.restaurant.di.qualifier.ApplicationContext
import javax.inject.Singleton

@Module(
    includes = [CrashlyticsModule::class, TimberModule::class, AndroidSupportInjectionModule::class,
        ActivityBuilderModule::class,
        NetworkModule::class, PersistenceModule::class]
)
abstract class ApplicationModule {
    @Binds
    @Singleton
    abstract fun bindApplication(application: MyApplication): Application

    @Binds
    @Singleton
    @ApplicationContext
    abstract fun bindApplicationContext(application: Application): Context
}