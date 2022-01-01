package ps.altariq.restaurant.data.model

import java.util.*

data class Order(
    var orD_ID: Int? = null,
    ///// here i did some change
    var orD_TableID: Int = -1,
    var orD_CustomerID: Int = -1,
    var orD_UserSerial: Int? = -1,
    var orD_Status: Int? = 0,
    var orD_Total: Double? = 0.0,
    var orD_StartTime: Date? = null,

    var orD_NOTES: String? = null,
    var orD_CustomerName: String? = null,
    var orD_Table: String? = null,
    var orD_Time: String? = null,

    var orderDetails: List<OrderDetails>

)





