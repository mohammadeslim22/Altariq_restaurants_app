package ps.altariq.restaurant.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealOptionsDao {
    @Query("SELECT * FROM mealOptions ORDER BY melId")
    fun getAll(): MutableList<MealOptions>

    @Query("SELECT * FROM mealOptions WHERE melId = :melId")
    fun getOptions(melId: Int): MutableList<MealOptions>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<MealOptions>)

    @Query("DELETE FROM mealOptions")
    fun deleteAll()
}
