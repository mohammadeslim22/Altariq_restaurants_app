package ps.altariq.restaurant.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table")
data class Table(
    @PrimaryKey @ColumnInfo(name = "id") var decId: Int, var decTableNo: String

)