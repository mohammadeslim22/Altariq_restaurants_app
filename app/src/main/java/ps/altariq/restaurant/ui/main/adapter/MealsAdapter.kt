package ps.altariq.restaurant.ui.main.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_single_card.view.*
import net.cachapa.expandablelayout.ExpandableLayout
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Meal
import ps.altariq.restaurant.data.model.OrderDetails
import ps.altariq.restaurant.ui.common.dialog.MealDialogFragment
import ps.altariq.restaurant.ui.main.MainActivity


class MealsAdapter(
    private val mContext: Context,
    private val orders: MutableLiveData<MutableList<OrderDetails>>,
    private val expandable_layout_1: ExpandableLayout
) :
    ListAdapter<Meal, MealsAdapter.ViewHolder>(MealDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sec = getItem(position)
        holder.apply {
            bind(sec, mContext, orders, expandable_layout_1)
            itemView.tag = sec
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_single_card, parent, false))
    }

    class ViewHolder(
        itemView: View?
    ) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(
            data: Meal,
            context: Context,
            orders: MutableLiveData<MutableList<OrderDetails>>, expandable_layout_1: ExpandableLayout
        ) {
            itemView.tvTitle.text = data.melArbName
            itemView.tvPrice.text = data.melPrice.toString()

            if (data.Image != null) {
                val bmp = BitmapFactory.decodeByteArray(data.Image, 0, data.Image!!.size)

                itemView.itemImage.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        bmp, 100,
                        100, false
                    )
                )
            } else
                itemView.itemImage.setImageResource(R.drawable.ic_meal)


            itemView.setOnLongClickListener {

                if (expandable_layout_1.isExpanded) {
                    showDetails(data, context)
                }
                true
            }

            itemView.setOnClickListener {

                if (expandable_layout_1.isExpanded) {
                    addToOrder(orders, data)
                } else {
                    showDetails(data, context)
                }
            }
        }

        private fun showDetails(data: Meal, context: Context) {

            if (data.Image == null) {
            } else {
                val bmp = BitmapFactory.decodeByteArray(data.Image, 0, data.Image!!.size)

            val fragment = MealDialogFragment()

            fragment.setData(

                data.melArbName, data.melDesc, Bitmap.createScaledBitmap(
                    bmp, 1000,
                    1000, false
                )
            )
            fragment.show((context as MainActivity).supportFragmentManager, "dialog")
        }
        }

        private fun addToOrder(
            orders: MutableLiveData<MutableList<OrderDetails>>,
            data: Meal
        ) {
            val o = orders.value!!.find { m -> m.melArbName == data.melArbName && m.ordD_Price == data.melPrice }

            if (o == null) {

                val order = OrderDetails()

                order.ordD_MealID = data.melId
                order.ordD_Price = data.melPrice
                order.melArbName = data.melArbName
                order.ordD_Quantity = 1.0
                order.ordD_Total = data.melPrice


                orders.value!!.add(0, order)


            } else {
                ///////// here i did some change
                o.ordD_Quantity += 1
                o.ordD_Total = o.ordD_Price * o.ordD_Quantity
            }
            orders.postValue(orders.value!!)
        }


    }
}

private class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {

    override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem.melId == newItem.melId
    }

    override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem == newItem
    }
}