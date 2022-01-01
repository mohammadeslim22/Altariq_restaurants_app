package ps.altariq.restaurant.data.model

data class OrderDetails(
    var ordD_Serial: Int? = null,

    var ordD_OrderID: Int = -1,

    var ordD_TableID: Int = -1,

    var ordD_MealID: Int = -1,

    var ordD_CustomerID: Int = -1,

    var ordD_Status: Int = 0,

    var ordD_Quantity: Double = 0.0,

    var ordD_Total: Double = 0.0,

    var ordD_Price: Double = 0.0,

    var ordD_Notes: String? = null,

    var ordD_DeleteReason: String? = null,

    var melArbName: String? = null,

    var options: MutableList<Option>? = null
)





