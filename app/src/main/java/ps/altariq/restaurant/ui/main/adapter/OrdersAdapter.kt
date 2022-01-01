package ps.altariq.restaurant.ui.main.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.signalr.HubConnection
import kotlinx.android.synthetic.main.orders_item.view.*
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Order
import ps.altariq.restaurant.ui.common.helper.Broker
import ps.altariq.restaurant.ui.main.MainActivity
import ps.altariq.restaurant.ui.main.fragment.AuthenticatorFragment
import timber.log.Timber
import javax.inject.Inject


class OrdersAdapter(

    private val mContext: Context,
    private val sharedPreferences: SharedPreferences
) :
    ListAdapter<Order, OrdersAdapter.ViewHolder>(OrdersDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)

        holder.apply {
            bind(data, mContext, sharedPreferences)
            itemView.tag = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.orders_item, parent, false)
        )
    }

    class ViewHolder(
        itemView: View?
    ) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(
            data: Order,
            context: Context,
            sharedPreferences: SharedPreferences
        ) {

//            GlobalScope.launch {
//                itemView.tv_customer.text = customerDao.getCustomer(data.orD_CustomerID).benName
//            }
            itemView.tv_status.text = if (data.orD_Status == 10) "بالإنتظار" else "جاهز"

            if (data.orD_Status == 10)
                itemView.tv_status.setTextColor(Color.YELLOW)
            else
                itemView.tv_status.setTextColor(Color.GREEN)


////////////// here i did some change
           // data.orD_UserSerial = AuthenticatorFragment.toString()
            if (data.orD_UserSerial.toString() == sharedPreferences.getString("userId", null))
            {
                itemView.tv_time.text = data.orD_Time
                itemView.tv_orderNo.text = data.orD_ID.toString()
                itemView.tv_total.text = data.orD_Total.toString()
                itemView.tv_table.text = data.orD_Table
                itemView.tv_customer.text = data.orD_CustomerName
            }
            else
            {

            }

            itemView.setOnClickListener {
                (context as MainActivity).mDrawer.closeMenu()
                sharedPreferences.edit().putInt("order_id", data.orD_ID!!).apply()
                Broker.selectedOrder = data
            }
        }
    }
}

private class OrdersDiffCallback : DiffUtil.ItemCallback<Order>() {

    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orD_ID == newItem.orD_ID && oldItem.orD_Status == newItem.orD_Status
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}