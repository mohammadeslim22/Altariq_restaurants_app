package ps.altariq.restaurant.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import ps.altariq.restaurant.MyApplication
import ps.altariq.restaurant.di.module.ApplicationModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MyApplication): Builder

        fun build(): ApplicationComponent
    }
}