package ps.altariq.restaurant.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.material.snackbar.Snackbar
import com.microsoft.signalr.HubConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.swipe_refresh
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ps.altariq.restaurant.MyApplication
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.Order
import ps.altariq.restaurant.ui.base.fragment.BaseFragment
import ps.altariq.restaurant.ui.common.helper.LocaleManager.Companion.LANGUAGE_KEY
import ps.altariq.restaurant.ui.main.MainActivity
import ps.altariq.restaurant.ui.main.adapter.OrdersAdapter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MenuListFragment : BaseFragment() {

    override val layoutViewRes: Int = R.layout.fragment_menu

    @Inject
    protected lateinit var rxSharedPreferences: RxSharedPreferences

    @Inject
    protected lateinit var sharedPreferences: SharedPreferences

    private var disposables: CompositeDisposable? = null

    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var orders: MutableLiveData<MutableList<Order>>

    @Inject
    lateinit var hubConnection: HubConnection


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposables = CompositeDisposable()

        orders = MutableLiveData()

        orders.value = ArrayList()

        ordersAdapter = OrdersAdapter(activity!!, sharedPreferences)

        ordersAdapter.submitList(orders.value)


        orders.observe(this, Observer {
            ordersAdapter.notifyDataSetChanged()
        })

        hubConnection.on("UpdateOrder", { list ->
            (activity as MainActivity).runOnUiThread {
                try {
                    list.forEach {
                        Timber.e("orders : $it")
                    }
                    orders.value!!.clear()

                    orders.value!!.addAll(list.toMutableList())
                    orders.postValue(orders.value!!)

                    swipe_refresh.isRefreshing = false

                } catch (e: Exception) {
                    e.printStackTrace()
                    swipe_refresh.isRefreshing = false
                }

            }

        }, Array<Order>::class.java)

        orders_rv.setHasFixedSize(false)
        orders_rv.layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        orders_rv.itemAnimator = DefaultItemAnimator()
        orders_rv.adapter = ordersAdapter

        updateList()

        swipe_refresh.setOnRefreshListener {
            updateList()
        }

    }

    private fun updateList() {
        try {
            swipe_refresh.isRefreshing = true

            disposables?.add(
                hubConnection.invoke(
                    Array<Order>::class.java,
                    "getOrders"
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ list ->
                    try {
                        list.forEach {
                            Timber.e("orders : $it")
                        }
                        orders.value!!.clear()

                        orders.value!!.addAll(list.toMutableList())
                        orders.postValue(orders.value!!)

                        swipe_refresh.isRefreshing = false

                    } catch (e: Exception) {
                        e.printStackTrace()
                        swipe_refresh.isRefreshing = false
                    }

                }, {
                    it.printStackTrace()
                    swipe_refresh.isRefreshing = false
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            swipe_refresh.isRefreshing = false

            GlobalScope.launch {
                try {
                    hubConnection.start().blockingAwait()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun setNewLocale(language: String, restartProcess: Boolean): Boolean {

        sharedPreferences.edit().putString(LANGUAGE_KEY, language).commit()
        (activity!!.applicationContext as MyApplication).localeManager.updateResources(
            (activity as MainActivity),
            language
        )

        val i = Intent((activity as MainActivity), MainActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

        if (restartProcess) {
            System.exit(0)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables?.dispose()
    }
    private fun showError(msg: String) {
        val snackbar = Snackbar
            .make(menu_rv, msg, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(Color.parseColor("#f44336"))
        val tv = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        tv.gravity = Gravity.CENTER_HORIZONTAL
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 18f
        snackbar.show()
    }
}
