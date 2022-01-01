package ps.altariq.restaurant.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY id")
    fun getAll(): MutableList<Customer>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomer(customerId: Int): Customer

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Customer>)

    @Query("DELETE FROM customers")
    fun deleteAll()
}
