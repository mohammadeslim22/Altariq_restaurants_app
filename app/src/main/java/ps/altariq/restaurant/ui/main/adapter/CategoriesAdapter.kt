package ps.altariq.restaurant.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import kotlinx.android.synthetic.main.list_item.view.*
import net.cachapa.expandablelayout.ExpandableLayout
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.OrderDetails
import ps.altariq.restaurant.data.model.SectionDataModel


class CategoriesAdapter(
    private val mContext: Context,
    private val orders: MutableLiveData<MutableList<OrderDetails>>,
    private val expandable_layout_1: ExpandableLayout
) :
    ListAdapter<SectionDataModel, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sec = getItem(position)
        holder.apply {
            bind(sec, mContext, orders, expandable_layout_1)
            itemView.tag = sec
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false))
    }

    class ViewHolder(
        itemView: View?
    ) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(
            data: SectionDataModel,
            context: Context,
            orders: MutableLiveData<MutableList<OrderDetails>>,expandable_layout_1: ExpandableLayout
        ) {
            val sectionName = data.headerTitle

            val singleSectionItems = data.allItemsInSection

            itemView.itemTitle.text = sectionName

            val itemListDataAdapter = MealsAdapter(context, orders, expandable_layout_1)

            itemListDataAdapter.submitList(singleSectionItems)

            itemView.meals_rv.setHasFixedSize(false)
            itemView.meals_rv.adapter = itemListDataAdapter

            itemView.meals_rv.isNestedScrollingEnabled = false

            itemView.meals_rv.layoutManager = CardSliderLayoutManager(context)

            CardSnapHelper().attachToRecyclerView(itemView.meals_rv)

            itemView.meals_rv.onFlingListener = null
        }
    }
}

private class CategoryDiffCallback : DiffUtil.ItemCallback<SectionDataModel>() {

    override fun areItemsTheSame(oldItem: SectionDataModel, newItem: SectionDataModel): Boolean {
        return oldItem.headerTitle == newItem.headerTitle
    }

    override fun areContentsTheSame(oldItem: SectionDataModel, newItem: SectionDataModel): Boolean {
        return oldItem == newItem
    }
}