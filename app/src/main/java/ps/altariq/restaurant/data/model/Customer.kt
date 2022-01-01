package ps.altariq.restaurant.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey @ColumnInfo(name = "id") var benNo: Int, var benName: String

)