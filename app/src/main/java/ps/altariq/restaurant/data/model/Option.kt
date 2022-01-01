package ps.altariq.restaurant.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "options"
)
data class Option(
    @PrimaryKey @ColumnInfo(name = "id") var optId: Int,
    var optName: String?,
    var optDefaultValue: Int

)