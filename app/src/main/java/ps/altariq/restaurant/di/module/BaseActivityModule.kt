@file:Suppress("unused")

package ps.altariq.restaurant.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ps.altariq.restaurant.di.qualifier.ActivityContext
import ps.altariq.restaurant.di.scope.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class BaseActivityModule {
    @Binds
    @ActivityScope
    @ActivityContext
    abstract fun bindActivityContext(activity: AppCompatActivity): Context

}