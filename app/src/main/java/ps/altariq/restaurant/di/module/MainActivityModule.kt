@file:Suppress("unused")

package ps.altariq.restaurant.di.module

import androidx.appcompat.app.AppCompatActivity
import ps.altariq.restaurant.di.scope.ActivityScope
import ps.altariq.restaurant.ui.main.MainActivity
import dagger.Binds
import dagger.Module

@Module(includes = [BaseActivityModule::class, BaseFragmentModule::class])
abstract class MainActivityModule {
    @Binds
    @ActivityScope
    abstract fun bindActivity(activity: MainActivity): AppCompatActivity

}
