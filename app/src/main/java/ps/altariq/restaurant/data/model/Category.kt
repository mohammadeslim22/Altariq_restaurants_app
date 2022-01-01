package ps.altariq.restaurant.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey @ColumnInfo(name = "id") var catId: Int, var catArbName: String?,
    var catEngName: String?,
    var catOrder: Int?,
    var catStatus: Int?,
    var catNotes: String?
)