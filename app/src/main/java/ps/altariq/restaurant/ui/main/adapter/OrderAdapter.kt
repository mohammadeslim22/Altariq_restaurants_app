package ps.altariq.restaurant.ui.main.adapter

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.microsoft.signalr.HubConnection
import io.fabric.sdk.android.services.common.CommonUtils
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.order_item.view.*
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Order
import ps.altariq.restaurant.data.model.OrderDetails


class OrderAdapter(
    private val mContext: Context,
    private val hubConnection: HubConnection,
    private val orders: MutableLiveData<MutableList<OrderDetails>>
   // private val orders2: MutableLiveData<MutableList<Order>>
) :
    ListAdapter<OrderDetails, OrderAdapter.ViewHolder>(OrderDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)

        holder.myCustomEditTextListener.updatePosition(position)

        holder.apply {
            bind(data, mContext)
            itemView.tag = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false),
            MyCustomEditTextListener()
        )
    }

    class ViewHolder(
        itemView: View?,
        var myCustomEditTextListener: MyCustomEditTextListener
    ) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(data: OrderDetails, context: Context) {

            itemView.order_item_name.text = data.melArbName
            itemView.order_item_price.text = data.ordD_Price.toString()


            if (data.ordD_Status != 0) {
                itemView.order_item_quantity.maxQuantity = data.ordD_Quantity.toInt()
                itemView.btn_item_notes.visibility = View.INVISIBLE

            } else {
                itemView.order_item_quantity.maxQuantity = Int.MAX_VALUE
                itemView.btn_item_notes.visibility = View.VISIBLE
            }

            itemView.order_item_quantity.setQuantity(data.ordD_Quantity.toInt())


            when (data.ordD_Status) {
                30 -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.material_green_100))
                else -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.cardview_light_background))
            }
            itemView.order_item_quantity.setTextChangedListener(myCustomEditTextListener)

            itemView.btn_item_notes.setOnClickListener {

                val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog_note, null)

                val editText: EditText = inflate.findViewById(R.id.custom_edit)
                editText.setTextColor(Color.BLACK)
                editText.setLines(10)
                editText.setBackgroundResource(R.drawable.edittext_bg)

                val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("ملاحظات الطلب").setCustomView(inflate).setConfirmText("حفظ")
                    .setConfirmClickListener {

                        data.ordD_Notes = editText.text.toString()

                        hideKeyboard(context, editText)
                        it.dismissWithAnimation()
                    }

                dialog.setCancelable(false)

                editText.setText(data.ordD_Notes)
                dialog.show()
            }
        }



    }

    inner class MyCustomEditTextListener : TextWatcher {
        private var position: Int = 0

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {

        }

        override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {

            if (getItem(position).ordD_Quantity > s.toString().toDouble() && getItem(position).ordD_Status != 0)
                try {
                    SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("هل أنت متأكد؟")
                        .setConfirmText("نعم")
                        .setCancelText("لا")
                        .setConfirmClickListener {

                            it.dismissWithAnimation()

                            val inflate: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_note, null)

                            val editText: EditText = inflate.findViewById(R.id.custom_edit)
                            editText.setTextColor(Color.BLACK)
                            editText.setLines(10)
                            editText.setBackgroundResource(R.drawable.edittext_bg)

                            val dialog = SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("سبب الحذف").setCustomView(inflate).setConfirmText("تأكيد")
                                .setConfirmClickListener { d ->

                                    CommonUtils.hideKeyboard(mContext, editText)
                                    hubConnection.send(
                                        "reduceQuantity",
                                        getItem(position).ordD_Serial,
                                        s.toString().toDouble(),
                                        editText.text.toString()
                                    )
                                    d.dismissWithAnimation()
                                }

                            dialog.setCancelable(false)
                            dialog.show()

                        }.setCancelClickListener {
                            it.cancel()
                            return@setCancelClickListener
                        }.show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            if (orders.value?.get(position)?.ordD_Quantity != s.toString().toDouble()) {

                orders.value?.get(position)?.ordD_Quantity = s.toString().toDouble()
                orders.value?.get(position)?.ordD_Total = s.toString().toDouble() * getItem(position).ordD_Price

                orders.postValue(orders.value!!)
            }
        }

        override fun afterTextChanged(editable: Editable) {

        }
    }
}


///////////////////////////// here i did some change
//
//    ListAdapter<Order, OrderAdapter.ViewHolder>(OrderDiffCallback()), Parcelable {
//
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val data = getItem(position)
//
//        holder.myCustomEditTextListener.updatePosition(position)
//
//        holder.apply {
//            bind(data, mContext)
//            itemView.tag = data
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        return ViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false),
//            MyCustomEditTextListener()
//        )
//    }
//
//    class ViewHolder(
//        itemView: View?,
//        var myCustomEditTextListener: MyCustomEditTextListener
//    ) : RecyclerView.ViewHolder(itemView!!) {
//
//        fun bind(data: OrderDetails, context: Context) {
//
//            itemView.order_item_name.text = data.melArbName
//            itemView.order_item_price.text = data.ordD_Price.toString()
//
//
//            if (data.ordD_Status != 0) {
//                itemView.order_item_quantity.maxQuantity = data.ordD_Quantity.toInt()
//                itemView.btn_item_notes.visibility = View.INVISIBLE
//
//            } else {
//                itemView.order_item_quantity.maxQuantity = Int.MAX_VALUE
//                itemView.btn_item_notes.visibility = View.VISIBLE
//            }
//
//            itemView.order_item_quantity.setQuantity(data.ordD_Quantity.toInt())
//
//
//            when (data.ordD_Status) {
//                20 -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.material_green_100))
//                else -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.cardview_light_background))
//            }
////            when (data.) {
////                20 -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.material_green_100))
////                else -> itemView.order_background.setCardBackgroundColor(context.resources.getColor(R.color.cardview_light_background))
////            }
//
//            itemView.order_item_quantity.setTextChangedListener(myCustomEditTextListener)
//
//            itemView.btn_item_notes.setOnClickListener {
//
//                val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog_note, null)
//
//                val editText: EditText = inflate.findViewById(R.id.custom_edit)
//                editText.setTextColor(Color.BLACK)
//                editText.setLines(10)
//                editText.setBackgroundResource(R.drawable.edittext_bg)
//
//                val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
//                    .setTitleText("ملاحظات الطلب").setCustomView(inflate).setConfirmText("حفظ")
//                    .setConfirmClickListener {
//
//                        data.ordD_Notes = editText.text.toString()
//
//                        hideKeyboard(context, editText)
//                        it.dismissWithAnimation()
//                    }
//
//                dialog.setCancelable(false)
//
//                editText.setText(data.ordD_Notes)
//                dialog.show()
//            }
//        }
//
//
//
//    }
//
//    inner class MyCustomEditTextListener : TextWatcher {
//        private var position: Int = 0
//
//        fun updatePosition(position: Int) {
//            this.position = position
//        }
//
//        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
//
//        }
//
//        override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
//
//            if (1 > s.toString().toDouble() && getItem(position).orD_Status != 0)
//                try {
//                    SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("هل أنت متأكد؟")
//                        .setConfirmText("نعم")
//                        .setCancelText("لا")
//                        .setConfirmClickListener {
//
//                            it.dismissWithAnimation()
//
//                            val inflate: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_note, null)
//
//                            val editText: EditText = inflate.findViewById(R.id.custom_edit)
//                            editText.setTextColor(Color.BLACK)
//                            editText.setLines(10)
//                            editText.setBackgroundResource(R.drawable.edittext_bg)
//
//                            val dialog = SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
//                                .setTitleText("سبب الحذف").setCustomView(inflate).setConfirmText("تأكيد")
//                                .setConfirmClickListener { d ->
//
//                                    CommonUtils.hideKeyboard(mContext, editText)
//                                    hubConnection.send(
//                                        "reduceQuantity",
//                                        getItem(position).orD_UserSerial,
//                                        s.toString().toDouble(),
//                                        editText.text.toString()
//                                    )
//                                    d.dismissWithAnimation()
//                                }
//
//                            dialog.setCancelable(false)
//                            dialog.show()
//
//                        }.setCancelClickListener {
//                            it.cancel()
//                            return@setCancelClickListener
//                        }.show()
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            if (orders.value?.get(position)?.ordD_Quantity != s.toString().toDouble()) {
//
//                orders.value?.get(position)?.ordD_Quantity = s.toString().toDouble()
//                orders.value?.get(position)?.ordD_Total = s.toString().toDouble() * getItem(position).ordD_Price
//
//                orders.postValue(orders.value!!)
//            }
//        }
//
//        override fun afterTextChanged(editable: Editable) {
//
//        }
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<OrderAdapter> {
//        override fun createFromParcel(parcel: Parcel): OrderAdapter {
//            return OrderAdapter(parcel)
//        }
//
//        override fun newArray(size: Int): Array<OrderAdapter?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
///////////////////////////// here i did some change

private class OrderDiffCallback : DiffUtil.ItemCallback<OrderDetails>() {

    override fun areItemsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem.ordD_MealID == newItem.ordD_MealID && oldItem.ordD_Quantity == newItem.ordD_Quantity && oldItem.ordD_Price == newItem.ordD_Price
    }

    override fun areContentsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem == newItem
    }
}

//private class OrderDiffCallback2 : DiffUtil.ItemCallback<Order>() {
//
//    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
//        return true;
//    }
//
//    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
//        return oldItem == newItem
//    }
//}