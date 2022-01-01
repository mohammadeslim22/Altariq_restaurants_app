package ps.altariq.restaurant.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CatDao {
    @Query("SELECT * FROM categories ORDER BY id")
    fun getCats(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :catId")
    fun getCat(catId: String): LiveData<Category>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(cats: List<Category>)

    @Query("DELETE FROM categories")
    fun deleteAll()

    @Update
    fun update(vararg cat: Category)
}
