package ps.altariq.restaurant.data.model

import androidx.room.*

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY melCatId")
    fun getMeals(): List<Meal>

    @Query("SELECT * FROM meals WHERE melCatId = :catId ORDER BY melCatId")
    fun getCatMeals(catId: Int): MutableList<Meal>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMeal(mealId: String): Meal

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(meals: List<Meal>)

    @Query("DELETE FROM meals")
    fun deleteAll()

    @Update
    fun update(vararg meal: Meal)
}
