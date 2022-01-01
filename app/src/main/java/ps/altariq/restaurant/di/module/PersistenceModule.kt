package ps.altariq.restaurant.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ps.altariq.restaurant.data.local.AppDatabase
import ps.altariq.restaurant.data.model.*
import ps.altariq.restaurant.di.qualifier.ApplicationContext
import javax.inject.Singleton

@Module
object PersistenceModule {

    @Provides
    @Singleton
    @JvmStatic
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "restaurant-db").build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideMealDao(database: AppDatabase): MealDao =
        database.mealDao()

    @Provides
    @Singleton
    @JvmStatic
    fun provideCatDao(database: AppDatabase): CatDao =
        database.catDao()

    @Provides
    @Singleton
    @JvmStatic
    fun provideCustomerDao(database: AppDatabase): CustomerDao =
        database.customerDao()

    @Provides
    @Singleton
    @JvmStatic
    fun provideTableDao(database: AppDatabase): TableDao =
        database.tableDao()

    @Provides
    @Singleton
    @JvmStatic
    fun provideMealOptionsDao(database: AppDatabase): MealOptionsDao =
        database.mealOptionsDao()

    @Provides
    @Singleton
    @JvmStatic
    fun provideOptionDao(database: AppDatabase): OptionDao =
        database.optionDao()
}