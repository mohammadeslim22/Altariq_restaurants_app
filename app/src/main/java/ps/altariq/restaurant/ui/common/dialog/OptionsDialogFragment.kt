package ps.altariq.restaurant.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.ms_square.etsyblur.BlurDialogFragment
import com.stone.vega.library.VegaLayoutManager
import mehdi.sakout.fancybuttons.FancyButton
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Option
import ps.altariq.restaurant.ui.main.adapter.OptionsAdapter


class OptionsDialogFragment : BlurDialogFragment() {

    private lateinit var mContext: Context
    private lateinit var options: MutableList<Option>


    fun setData(options: MutableList<Option>, mContext: Context) {
        this.options = options
        this.mContext = mContext

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

//        val builderSingle = AlertDialog.Builder(activity!!, ps.altariq.restaurant.R.style.EtsyBlurAlertDialogTheme)
//
//        val dialog = builderSingle.create()
        dialog.window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes?.width = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(ps.altariq.restaurant.R.layout.options_dialog)

        val rv = dialog.findViewById(ps.altariq.restaurant.R.id.options_rv) as RecyclerView

        val b = dialog.findViewById(ps.altariq.restaurant.R.id.btn_save) as FancyButton

        rv.setHasFixedSize(false)

        val adapter = OptionsAdapter(mContext)

        adapter.submitList(options)

        rv.layoutManager = VegaLayoutManager()
        rv.adapter = adapter

        b.setOnClickListener {
            dialog.dismiss()
        }

        return dialog

    }
}
