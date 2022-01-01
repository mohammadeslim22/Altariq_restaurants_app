package ps.altariq.restaurant.ui.common.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.ms_square.etsyblur.BlurDialogFragment
import mehdi.sakout.fancybuttons.FancyButton
import ps.altariq.restaurant.R


class MealDialogFragment : BlurDialogFragment() {
    private var name: String? = null
    private var desc: String? = null
    private var bitmap: Bitmap? = null


    fun setData(name: String?, desc: String?, bitmap: Bitmap?) {
        this.name = name
        this.desc = desc
        this.bitmap = bitmap

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

        dialog.setContentView(ps.altariq.restaurant.R.layout.image_dialog)

        val n = dialog.findViewById(ps.altariq.restaurant.R.id.tv_name) as TextView
        val d = dialog.findViewById(ps.altariq.restaurant.R.id.tv_desc) as TextView
        val i = dialog.findViewById(ps.altariq.restaurant.R.id.image_view) as ImageView
        val b = dialog.findViewById(ps.altariq.restaurant.R.id.btn_close) as FancyButton

        n.text = name
        d.text = desc

        if (bitmap != null)
            i.setImageBitmap(bitmap)

        b.setOnClickListener {
            dialog.dismiss()
        }

        return dialog

    }
}
