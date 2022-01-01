package ps.altariq.restaurant.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OptionDao {
    @Query("SELECT * FROM options ORDER BY id")
    fun getAll(): MutableList<Option>

    @Query("SELECT * FROM options WHERE id = :optId")
    fun getOption(optId: Int): Option

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Option>)

    @Query("DELETE FROM options")
    fun deleteAll()
}
