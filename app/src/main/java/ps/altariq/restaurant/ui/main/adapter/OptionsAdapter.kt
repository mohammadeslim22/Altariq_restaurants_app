package ps.altariq.restaurant.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.option_item.view.*
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Option

class OptionsAdapter(private val mContext: Context) :
    ListAdapter<Option, OptionsAdapter.ViewHolder>(OptionsDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = getItem(position)

        holder.apply {
            bind(data, mContext)
            itemView.tag = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.option_item, parent, false)
        )
    }

    class ViewHolder(
        itemView: View?
    ) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(
            data: Option,
            context: Context
        ) {

            itemView.option_name.text = data.optName
            itemView.seekBar.max = 10
            itemView.seekBar.progress = data.optDefaultValue
           // Toast.makeText(context, "Progress : $data", Toast.LENGTH_SHORT).show()

            itemView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    data.optDefaultValue = i
                    //Toast.makeText(context, "data.optDefaultValue : ${data.optDefaultValue}", Toast.LENGTH_SHORT).show()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //   Toast.makeText(context, "start tracking", Toast.LENGTH_SHORT).show()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
        }
    }
}

private class OptionsDiffCallback : DiffUtil.ItemCallback<Option>() {

    override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem.optId == newItem.optId
    }

    override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem == newItem
    }
}