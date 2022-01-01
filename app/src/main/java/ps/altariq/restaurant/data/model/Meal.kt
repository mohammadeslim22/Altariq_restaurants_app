package ps.altariq.restaurant.data.model

import androidx.room.*

@Entity(
    tableName = "meals", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["melCatId"]
    )],
    indices = [Index("melCatId")]
)
data class Meal(
    @PrimaryKey @ColumnInfo(name = "id") var melId: Int,
    var melArbName: String?,
    var melEngName: String?,
    var melCatId: Int,
    var melOrder: Int?,
    var melPrice: Double,
    var melLogo: Int?,
    var melDesc: String?,
    var melDefaultKitchen: Int,
    var melStatus: Int,
    var melPrice2: Double?,
    var Image: ByteArray?
)