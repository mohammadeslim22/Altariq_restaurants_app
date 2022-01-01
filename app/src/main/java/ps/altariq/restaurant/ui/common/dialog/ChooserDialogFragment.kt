package ps.altariq.restaurant.ui.common.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import com.ms_square.etsyblur.BlurDialogFragment
import ps.altariq.restaurant.R


class ChooserDialogFragment : BlurDialogFragment() {
    internal lateinit var statusListener: DialogInterface.OnClickListener


    fun setData(statusListener: DialogInterface.OnClickListener) {
        this.statusListener = statusListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // implement either onCreateView or onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builderSingle = AlertDialog.Builder(activity!!, R.style.EtsyBlurAlertDialogTheme)

//        val list = ArrayList<CardItem>()
//
//        list.add(CardItem(getString(R.string.take_photo), R.drawable.ic_camera_alt_white_24dp,1))
//        list.add(CardItem(getString(R.string.pick_photo), R.drawable.ic_folder_white_24dp,2))
//
//        val adapter = DialogAdapter((activity as FragmentActivity), list)
//
//        builderSingle.setAdapter(
//            adapter,
//            statusListener
//        )

        val dialog = builderSingle.create()
        dialog.window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes?.width = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners)
        //builderSingle.sh
        return dialog

    }

}
