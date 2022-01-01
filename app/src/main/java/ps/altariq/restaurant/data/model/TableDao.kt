package ps.altariq.restaurant.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TableDao {
    @Query("SELECT * FROM `table` ORDER BY id")
    fun getAll(): MutableList<Table>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Table>)

    @Query("DELETE FROM `table`")
    fun deleteAll()
}
