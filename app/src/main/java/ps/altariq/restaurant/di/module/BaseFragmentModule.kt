@file:Suppress("unused")

package ps.altariq.restaurant.di.module

import ps.altariq.restaurant.di.scope.FragmentScope
import dagger.Module
import dagger.Provides
import ps.altariq.restaurant.ui.common.helper.EmailValidator
import ps.altariq.restaurant.ui.common.helper.InputValidator

@Module
abstract class BaseFragmentModule {

    @Module
    companion object {

        @Provides
        @FragmentScope
        @JvmStatic
        fun provideInputValidator(): InputValidator = InputValidator()

        @Provides
        @FragmentScope
        @JvmStatic
        fun provideEmailValidator(): EmailValidator = EmailValidator()
    }
}
