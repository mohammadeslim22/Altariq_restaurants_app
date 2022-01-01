package ps.altariq.restaurant.ui.main.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dd.processbutton.iml.ActionProcessButton
import com.jakewharton.rxbinding2.widget.textChanges
import com.microsoft.signalr.HubConnection
import com.mxn.soul.flowingdrawer_core.MyElastic
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_authenticator.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ps.altariq.restaurant.ui.base.fragment.BaseFragment
import ps.altariq.restaurant.ui.main.MainActivity
import timber.log.Timber
import javax.inject.Inject


class AuthenticatorFragment : BaseFragment() {
    override val layoutViewRes: Int = ps.altariq.restaurant.R.layout.fragment_authenticator
///////////// here i did some change
    public var useridforcheck: String = ""
    private var disposables: CompositeDisposable? = null
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var hubConnection: HubConnection


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.progress = 0

        input_password.requestFocus()

        disposables = CompositeDisposable()


        hidden_button.setOnClickListener {
            val editText = EditText(activity)

            editText.setText(sharedPreferences.getString("BASE_URL", "http://22.12.68.127:1978/restaurant"))
            SweetAlertDialog.DARK_STYLE = false
            editText.setTextColor(Color.BLACK)
            val linearLayout = LinearLayout(activity!!.applicationContext)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(editText)


            val dialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("عنوان الخادم").setCustomView(linearLayout).setConfirmText("حفظ")
                .setConfirmClickListener {

                    sharedPreferences.edit().putString("BASE_URL", editText.text.toString()).apply()

                    (activity as MainActivity).hideKeyboard(editText)
                    it.dismiss()
                }
            dialog.show()
        }


        btn_login.setOnClickListener {

            try {
                btn_login.isEnabled = false
                btn_login.setMode(ActionProcessButton.Mode.ENDLESS)
                btn_login.progress = 1

                disposables?.add(
                    hubConnection.invoke(
                        Int::class.java,
                        "checkWaiter",
                        input_password.text.toString()

                    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ userId ->
                        try {
                            Timber.e("userId: %s", userId)

                            if (userId != null) {



                                useridforcheck = userId.toString()

                                sharedPreferences.edit().putString("userId", userId.toString()).apply()

                                btn_login.progress = 100

                                (activity as MainActivity).hideKeyboard(input_password)

                                val args = Bundle()
                                args.putBoolean("isLogged", true)
                                navController.navigate(
                                    ps.altariq.restaurant.R.id.action_authenticator_to_homeFragment,
                                    args
                                )

                            } else{
                                btn_login.progress = 0
                                btn_login.isEnabled = true
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            btn_login.progress = 0
                            btn_login.isEnabled = true
                        }
                    }, {
                        it.printStackTrace()

                        btn_login.progress = 0
                        btn_login.isEnabled = true

                        SweetAlertDialog((activity as MainActivity), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("كلمة المرور خطأ!")
                            .setConfirmText(getString(ps.altariq.restaurant.R.string.ok))
                            .setConfirmClickListener { sDialog ->
                                sDialog.dismissWithAnimation()
                                sDialog.cancel()
                            }.show()
                    })
                )

            } catch (e: Exception) {

                e.printStackTrace()

                GlobalScope.launch {
                    try {
                        hubConnection.start().blockingAwait()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                btn_login.progress = 0
                btn_login.isEnabled = true

                SweetAlertDialog((activity as MainActivity), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("خطأ في الإتصال!")
                    .setConfirmText(getString(ps.altariq.restaurant.R.string.ok))
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        sDialog.cancel()
                    }.show()
            }
        }

        disposables?.add(
            input_password.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    btn_login.isEnabled = !it.isEmpty()
                }
        )
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).mDrawer.setTouchMode(MyElastic.TOUCH_MODE_NONE)

        (activity as MainActivity).toolbar.visibility = View.GONE

    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposables?.dispose()
    }

    override fun toString(): String {
        return "AuthenticatorFragment(useridforcheck='$useridforcheck')"
    }
}