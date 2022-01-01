package ps.altariq.restaurant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "mealOptions", foreignKeys = [ForeignKey(
        entity = Option::class,
        parentColumns = ["id"],
        childColumns = ["optId"]
    ), ForeignKey(
        entity = Meal::class,
        parentColumns = ["id"],
        childColumns = ["melId"]
    )],
    indices = [Index("optId"), Index("melId")],
    primaryKeys = ["optId", "melId"]
)
data class MealOptions(
    var optId: Int,
    var melId: Int

)