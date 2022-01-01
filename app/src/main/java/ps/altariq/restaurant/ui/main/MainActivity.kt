package ps.altariq.restaurant.ui.main

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.microsoft.signalr.HubConnection
import com.ms_square.etsyblur.BlurringView
import com.mxn.soul.flowingdrawer_core.MyElastic
import com.mxn.soul.flowingdrawer_core.MyFlowingDrawer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ps.altariq.restaurant.R
import ps.altariq.restaurant.ui.base.activity.BaseToolbarActivity
import ps.altariq.restaurant.ui.base.fragment.BaseFragment
import ps.altariq.restaurant.ui.common.helper.retryIO
import ps.altariq.restaurant.ui.main.fragment.MenuListFragment
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseToolbarActivity() {
    override val toolbar: Toolbar by lazy { header }
    override var toolbarTitle: TextView? = null
    override val layoutViewRes: Int = R.layout.activity_main
    override val navController: NavController by lazy { findNavController(R.id.nav_host) }
    lateinit var mDrawer: MyFlowingDrawer

    private var disposables: Disposable? = null

    private lateinit var mMenuFragment: MenuListFragment

    @Inject
    protected lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var hubConnection: HubConnection

    private var isStarted: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        // launch screen theme from manifest
        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        GlobalScope.launch {
            retryIO {
                try {
                    hubConnection.start().blockingAwait()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val userId: String? = sharedPreferences.getString("userId", null)

        if (userId != null) {
            //navController.popBackStack()
            navController.navigate(ps.altariq.restaurant.R.id.action_global_homeFragment)
        }
    }

    override fun onStart() {
        super.onStart()

        mDrawer = drawerlayout

        if (!isStarted) {
            mDrawer.setTouchMode(MyElastic.TOUCH_MODE_NONE)
            sharedPreferences.edit().putInt("order_id", -1).apply()

            isStarted = true
        }

        setupMenu()

        (blurring_view as BlurringView).blurredView(main_layout)
        blurring_view.visibility = View.INVISIBLE
        blurring_view.alpha = 0.4f

        mDrawer.setOnDrawerStateChangeListener(object : MyElastic.OnDrawerStateChangeListener {
            override fun onDrawerStateChange(oldState: Int, newState: Int) {
                if (newState == MyElastic.STATE_CLOSED)
                    blurring_view.visibility = View.INVISIBLE
                else if (newState == MyElastic.STATE_OPEN || newState == MyElastic.STATE_DRAGGING_OPEN || newState == MyElastic.STATE_OPENING)
                    blurring_view.visibility = View.VISIBLE

            }

            override fun onDrawerSlide(openRatio: Float, offsetPixels: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()

        disposables = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                Timber.e("isConnectedToInternet: %s", connectivity.toString())

                if (!connectivity.available())
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.check_connection))
                        .setConfirmText(getString(R.string.ok))
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            sDialog.cancel()
                        }.show()
            }
    }

//    override fun attachBaseContext(base: Context) {
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(base)
//
//        super.attachBaseContext(
//            LocaleManager().updateResources(
//                base, sharedPreferences.getString(
//                    LocaleManager.LANGUAGE_KEY,
//                    Locale.getDefault().language
//                )
//            )
//        )
//
//    }

    override fun onPause() {
        super.onPause()
        if (disposables != null && !disposables!!.isDisposed) {
            disposables!!.dispose()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun verifyStoragePermissions() {

        try {

            val permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            val permission2 = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
            if (permission1 == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

        } catch (e: Exception) {

        }
    }

    private fun setupMenu() {

        val fm = supportFragmentManager

        mMenuFragment = MenuListFragment()

        fm.beginTransaction().replace(R.id.id_container_menu, mMenuFragment).commit()
    }

    fun hideKeyboard(view: View) {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onBackPressed() {

        var handled = false

        val childFragments = nav_host.childFragmentManager.fragments

        for (f in childFragments) {
            if (f is BaseFragment) {
                handled = f.onBackPressed()

                if (handled) {
                    break
                }
            }
        }

        if (!handled) {
            super.onBackPressed()
        }
    }

}
