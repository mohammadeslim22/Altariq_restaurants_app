@file:Suppress("unused")

package ps.altariq.restaurant.di.module

import ps.altariq.restaurant.ui.main.fragment.MenuListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ps.altariq.restaurant.di.scope.ActivityScope
import ps.altariq.restaurant.di.scope.FragmentScope
import ps.altariq.restaurant.ui.main.MainActivity
import ps.altariq.restaurant.ui.main.fragment.AuthenticatorFragment
import ps.altariq.restaurant.ui.main.fragment.HomeFragment


@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [BaseFragmentModule::class])
    @FragmentScope
    abstract fun contributeAuthenticatorFragment(): AuthenticatorFragment

    @ContributesAndroidInjector(modules = [BaseFragmentModule::class])
    @FragmentScope
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector(modules = [BaseFragmentModule::class])
    @FragmentScope
    abstract fun contributeMenuListFragment(): MenuListFragment

}
