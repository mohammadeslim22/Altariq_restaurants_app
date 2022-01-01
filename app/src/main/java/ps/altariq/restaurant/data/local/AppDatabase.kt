package ps.altariq.restaurant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ps.altariq.restaurant.data.model.*

/**
 * The Room database for this app
 */
@Database(entities = [Meal::class, Category::class, Customer::class, Table::class, MealOptions::class, Option::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun catDao(): CatDao
    abstract fun customerDao(): CustomerDao
    abstract fun tableDao(): TableDao
    abstract fun mealOptionsDao(): MealOptionsDao
    abstract fun optionDao(): OptionDao

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

    }
}