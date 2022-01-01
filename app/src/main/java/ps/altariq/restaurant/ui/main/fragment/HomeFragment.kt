package ps.altariq.restaurant.ui.main.fragment

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.android.material.snackbar.Snackbar
import com.microsoft.signalr.HubConnection
import com.mxn.soul.flowingdrawer_core.MyElastic
import com.stone.vega.library.VegaLayoutManager
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import dmax.dialog.SpotsDialog
import io.fabric.sdk.android.services.common.CommonUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ps.altariq.restaurant.R
import ps.altariq.restaurant.data.model.*
import ps.altariq.restaurant.ui.base.fragment.BaseFragment
import ps.altariq.restaurant.ui.common.dialog.OptionsDialogFragment
import ps.altariq.restaurant.ui.common.helper.Broker
import ps.altariq.restaurant.ui.main.MainActivity
import ps.altariq.restaurant.ui.main.adapter.CategoriesAdapter
import ps.altariq.restaurant.ui.main.adapter.OrderAdapter
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment() {
    override val layoutViewRes: Int = R.layout.fragment_home

    private var disposables: CompositeDisposable? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var hubConnection: HubConnection

    @Inject
    lateinit var mealDao: MealDao

    @Inject
    lateinit var catDao: CatDao

    @Inject
    lateinit var customerDao: CustomerDao

    @Inject
    lateinit var tableDao: TableDao

    @Inject
    lateinit var optionDao: OptionDao

    @Inject
    lateinit var mealOptionsDao: MealOptionsDao

    @Inject
    lateinit var rxSharedPreferences: RxSharedPreferences

    private lateinit var catsAdapter: CategoriesAdapter

    private lateinit var orderAdapter: OrderAdapter

    private lateinit var restaurantMenu: ArrayList<SectionDataModel>

    private lateinit var customers: MutableList<Customer>

    private lateinit var tables: MutableList<Table>

    private lateinit var orders: MutableLiveData<MutableList<OrderDetails>>

    private lateinit var tableNames: ArrayList<String>

    private lateinit var customerNames: ArrayList<String>

    private lateinit var customersAdapter: ArrayAdapter<String>

    private lateinit var tablesAdapter: ArrayAdapter<String>

    private var orderNotes: String? = null

    private lateinit var optionsMenu: Menu

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).toolbar.visibility = View.VISIBLE

        // (activity as MainActivity).toolbar.setNavigationIcon(R.mipmap.ic_menu_white)

        //(activity as MainActivity).toolbar.setNavigationOnClickListener { (activity as MainActivity).mDrawer.toggleMenu() }
        //(activity as MainActivity).toolbar.title = getString(R.string.home)

        //(activity as MainActivity).mDrawer.setTouchMode(MyElastic.TOUCH_MODE_NONE)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        disposables = CompositeDisposable()

        restaurantMenu = ArrayList()

        orders = MutableLiveData()

        orders.value = ArrayList()

        menu_rv.setHasFixedSize(false)
        catsAdapter = CategoriesAdapter(activity!!, orders, expandable_layout_1)
        menu_rv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        menu_rv.adapter = catsAdapter

        //////////////////////////////////////////////////////////////////////////////////
        orderAdapter = OrderAdapter(context!!, hubConnection, orders)

        orderAdapter.submitList(orders.value)

        orders.observe(this, Observer { list ->
            orderAdapter.submitList(list)
            orderAdapter.notifyDataSetChanged()

            var total = 0.0
            list.forEach { total += it.ordD_Total }

            order_total_price.text = total.toString()
        })


//        val date = Calendar.getInstance().time
//        val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH)
//        order_time.text = formatter.format(date)


        order_rv.setHasFixedSize(false)
        order_rv.setSwipeMenuCreator(swipeMenuCreator)
        order_rv.setOnItemMenuClickListener(mMenuItemClickListener)

        order_rv.layoutManager = VegaLayoutManager()
        order_rv.adapter = orderAdapter


        disposables?.add(
            rxSharedPreferences.getInteger("order_id").asObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ order_id ->
                    synchronized(mSync) {
                        if (expandable_layout_1.isExpanded) {
                            val dialog: AlertDialog =
                                SpotsDialog.Builder().setMessage("يتم عرض الطلب").setTheme(R.style.CustomDialogSpot)
                                    .setCancelable(false).setContext(activity).build()
                            dialog.show()
                            try {
//                            SweetAlertDialog(activity)
//                                .setTitleText("Order Id: $it")
//                                .show()

                                disposables?.add(
                                    hubConnection.invoke(
                                        Array<OrderDetails>::class.java,
                                        "getOrderDetails",
                                        order_id
                                    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ list ->
                                        try {
                                            list.forEach {
                                                Timber.e("OrderDetails : $it")
                                            }
                                            dialog.dismiss()

                                            if (!list.isNullOrEmpty()) {
                                                orders.value!!.clear()
                                                orders.value!!.addAll(list.toMutableList())
                                                orders.postValue(orders.value!!)
                                                order_time.text = Broker.selectedOrder?.orD_Time

                                                optionsMenu.findItem(R.id.action_send).isVisible = false
                                                optionsMenu.findItem(R.id.action_note).isVisible = false

                                                if (Broker.selectedOrder!!.orD_CustomerID != 1) {
                                                    customers.forEachIndexed { index, customer ->

                                                        if (Broker.selectedOrder!!.orD_CustomerID == customer.benNo) {
                                                            customersSpinner.setSelection(index + 1)
                                                        }
                                                    }
                                                }
                                                tables.forEachIndexed { index, table ->

                                                    if (Broker.selectedOrder!!.orD_TableID == table.decId) {
                                                        tablesSpinner.setSelection(index + 1)
                                                    }
                                                }
                                                //orD_TableID = tables[tablesSpinner.selectedItemPosition].decId,
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            dialog.dismiss()
                                            showError(getString(R.string.error))
                                        }

                                    }, {
                                        it.printStackTrace()
                                        dialog.dismiss()
                                        showError(getString(R.string.error))
                                    })
                                )

                            } catch (e: Exception) {
                                e.printStackTrace()
                                dialog.dismiss()
                                showError(e.printStackTrace().toString())

                            }
                        }
                    }
                }, {
                    it.printStackTrace()
                    showError(it.printStackTrace().toString())
                })
        )

        customerNames = ArrayList()
        tableNames = ArrayList()

        customersAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, customerNames)
        customersSpinner.adapter = customersAdapter

        tablesAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, tableNames)
        tablesSpinner.adapter = tablesAdapter

        customersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (activity as MainActivity).hideKeyboard(view!!)
            }
        }

        tablesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (activity as MainActivity).hideKeyboard(view!!)
            }
        }

        updateMenu()

        swipe_refresh.setOnRefreshListener {
            updateMenu()
        }

        btn_new.setOnClickListener {
            showOrder()

            if (!orders.value.isNullOrEmpty())
                SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("هل تريد فتح طلب جديد؟")
                    .setConfirmText("نعم")
                    .setCancelText("لا")
                    .setConfirmClickListener {
                        clearOrder()

                        it.dismissWithAnimation()

                    }.setCancelClickListener {
                        it.cancel()
                    }.show()
            else
                clearOrder()
        }
    }

    private fun clearOrder() {
        orders.value!!.clear()
        orders.postValue(orders.value)
        tablesSpinner.setSelection(0)
        customersSpinner.setSelection(0)
        order_time.text = ""
        optionsMenu.findItem(R.id.action_send).isVisible = true
        optionsMenu.findItem(R.id.action_note).isVisible = true
        Broker.selectedOrder = null
    }

    private fun showOrder() {
        if (!expandable_layout_1.isExpanded) {
            expandable_layout_1.expand()
            optionsMenu.findItem(R.id.action_expand).setIcon(R.drawable.baseline_fullscreen_24)
            (activity as MainActivity).mDrawer.setTouchMode(MyElastic.TOUCH_MODE_BEZEL)
            optionsMenu.findItem(R.id.action_view).isVisible = true
            optionsMenu.findItem(R.id.action_delete).isVisible = true
        }
    }

    private fun updateMenu() {

        disposables?.add(
            Completable.fromAction {
                try {
                    val cats = catDao.getCats()

                    restaurantMenu.clear()

                    cats.forEach { category: Category ->

                        val dm = SectionDataModel()

                        dm.headerTitle = category.catArbName

                        dm.allItemsInSection = mealDao.getCatMeals(category.catId)

                        restaurantMenu.add(dm)
                    }

                    ///////////////////////////////////////////////////////////////////////////////

                    customers = customerDao.getAll()

                    customerNames.clear()

                    customerNames.add("الزبون العام")

                    customers.forEach { customerNames.add(it.benName) }

                    ///////////////////////////////////////////////////////////////////////////////

                    tables = tableDao.getAll()

                    tableNames.clear()

                    tableNames.add("إختر طاولة")

                    tables.forEach { tableNames.add(" طاولة ${it.decTableNo} ") }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe({
                    catsAdapter.submitList(restaurantMenu)
                    catsAdapter.notifyDataSetChanged()
                    customersAdapter.notifyDataSetChanged()
                    tablesAdapter.notifyDataSetChanged()

                    swipe_refresh.isRefreshing = false

                    if (restaurantMenu.isNullOrEmpty())
                        sync()

                }, {
                    it.printStackTrace()
                    showError(getString(R.string.error))
                })
        )
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

    private fun showSuccess(msg: String) {
        val snackbar = Snackbar
            .make(menu_rv, msg, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(Color.parseColor("#37C000"))
        val tv = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        tv.gravity = Gravity.CENTER_HORIZONTAL
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.textSize = 18f
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()

        inflater.inflate(R.menu.home_menu, menu)

        optionsMenu = menu

        val myActionMenuItem = menu.findItem(R.id.action_search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(query: String): Boolean {

                if (!query.isBlank()) {
                    val filtered: ArrayList<SectionDataModel> = ArrayList()

                    restaurantMenu.forEach {
                        val dm = SectionDataModel()

                        dm.allItemsInSection = it.allItemsInSection!!.filter { meal ->
                            meal.melArbName!!.toLowerCase().contains(query)
                        }.toMutableList()

                        if (dm.allItemsInSection!!.isNotEmpty()) {

                            dm.headerTitle = it.headerTitle
                            filtered.add(dm)
                        }
                    }

                    catsAdapter.submitList(filtered)

                } else
                    catsAdapter.submitList(restaurantMenu)

                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {

                sharedPreferences.edit().putString("userId", null).apply()
                navController.navigate(ps.altariq.restaurant.R.id.action_global_authenticator)

                return false
            }
            R.id.action_search -> {

                TransitionManager.beginDelayedTransition((activity as MainActivity).toolbar as ViewGroup)

                return false
            }
            R.id.action_delete -> {

                try {
                    SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("هل أنت متأكد؟")
                        .setConfirmText("نعم")
                        .setCancelText("لا")
                        .setConfirmClickListener {

                            it.dismissWithAnimation()

                            if (Broker.selectedOrder != null) {

                                val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog_note, null)

                                val editText: EditText = inflate.findViewById(R.id.custom_edit)
                                editText.setTextColor(Color.BLACK)
                                editText.setLines(10)
                                editText.setBackgroundResource(R.drawable.edittext_bg)

                                val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("سبب الحذف").setCustomView(inflate).setConfirmText("تأكيد")
                                    .setConfirmClickListener { d ->

                                        CommonUtils.hideKeyboard(context, editText)
                                        hubConnection.send(
                                            "deleteOrder", Broker.selectedOrder!!.orD_ID, editText.text.toString()
                                        )
                                        d.dismissWithAnimation()
                                        clearOrder()
                                    }

                                dialog.setCancelable(false)
                                dialog.show()
                            } else {
                                clearOrder()
                            }
                        }.setCancelClickListener {
                            it.cancel()
                        }.show()


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return false
            }
            R.id.action_send -> {
                try {

                    hubConnection.start().blockingAwait()
                    if (!orders.value.isNullOrEmpty()) {
                        if (tablesSpinner.selectedItemPosition != 0) {
                            var total = 0.0
                            orders.value!!.forEach {
                                total += it.ordD_Total
                            }
///// here i did some change
                            hubConnection.send(

                                "SubmitOrder",
                                Order(

                                    orD_NOTES = orderNotes,
                                    orD_Total = total,
                                    orD_StartTime = Calendar.getInstance().time,
                                    orD_CustomerID = if (customersSpinner.selectedItemPosition == 0) 1 else customers[customersSpinner.selectedItemPosition - 1].benNo,
                                    orD_TableID = tables[tablesSpinner.selectedItemPosition-1].decId,

                                    orD_UserSerial = sharedPreferences.getString("userId", null)!!.toDouble().toInt(),
                                    orderDetails = orders.value!!
                                )
                            )

                            showSuccess("تم الإرسال إلى المطبخ")

                            orders.value!!.clear()
                            orders.postValue(orders.value)
                            tablesSpinner.setSelection(0)
                            customersSpinner.setSelection(0)

                        } else {
                            showError("إختر طاولة!")
                        }
                    } else {
                        showError("إختر وجبة!")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                   showError(getString(R.string.error))
                    GlobalScope.launch {
                        try {
                            hubConnection.start().blockingAwait()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                return false
            }
            R.id.action_note -> {

                val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog_note, order_rv, false)

                val editText: EditText = inflate.findViewById(R.id.custom_edit)
                editText.setTextColor(Color.BLACK)
                editText.setLines(10)
                editText.setBackgroundResource(R.drawable.edittext_bg)

                val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("ملاحظات الطلب").setCustomView(inflate).setConfirmText("حفظ")
                    .setConfirmClickListener {

                        orderNotes = editText.text.toString()

                        (activity as MainActivity).hideKeyboard(editText)
                        it.dismissWithAnimation()
                    }

                dialog.setCancelable(false)

                editText.setText(orderNotes)

                dialog.show()

                return false
            }
            R.id.action_view -> {

                (activity as MainActivity).mDrawer.openMenu()

                return false
            }
            R.id.action_expand -> {

                if (expandable_layout_1.isExpanded) {
                    expandable_layout_1.collapse()
                    item.setIcon(R.drawable.baseline_fullscreen_exit_24)
                    optionsMenu.findItem(R.id.action_send).isVisible = false
                    optionsMenu.findItem(R.id.action_delete).isVisible = false
                    optionsMenu.findItem(R.id.action_note).isVisible = false
                    optionsMenu.findItem(R.id.action_view).isVisible = false
                    (activity as MainActivity).mDrawer.setTouchMode(MyElastic.TOUCH_MODE_NONE)


                } else {
                    expandable_layout_1.expand()
                    item.setIcon(R.drawable.baseline_fullscreen_24)
                    optionsMenu.findItem(R.id.action_send).isVisible = true
                    optionsMenu.findItem(R.id.action_delete).isVisible = true
                    optionsMenu.findItem(R.id.action_note).isVisible = true
                    optionsMenu.findItem(R.id.action_view).isVisible = true
                    (activity as MainActivity).mDrawer.setTouchMode(MyElastic.TOUCH_MODE_BEZEL)


                }
                return false
            }
            R.id.action_sync -> {

                sync()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sync() {
        val dialog: AlertDialog =
            SpotsDialog.Builder().setMessage("يتم تحديث البيانات").setTheme(R.style.CustomDialogSpot)
                .setCancelable(false).setContext(activity).build()
        dialog.show()

        try {
            disposables?.add(
                hubConnection.invoke(Array<Category>::class.java, "getCategories")
                    .subscribeOn(Schedulers.computation()).subscribe({ cats ->
                        synchronized(mSync) {
                            try {
                                mealOptionsDao.deleteAll()
                                mealDao.deleteAll()
                                catDao.deleteAll()
                                customerDao.deleteAll()
                                tableDao.deleteAll()
                                optionDao.deleteAll()


                                catDao.insertAll(cats.toList())

                                cats.forEach { cat ->
                                    Timber.d("catDaoInserted new cat, ID: ${cat.catId}")
                                }

                                disposables?.add(
                                    hubConnection.invoke(
                                        Array<Customer>::class.java,
                                        "getCustomers"
                                    ).subscribeOn(Schedulers.computation()).subscribe({ customers ->
                                        synchronized(mSync) {
                                            try {
                                                customerDao.insertAll(customers.asList())

                                                customers.forEach {
                                                    Timber.d("Inserted new customer, ID: ${it.benNo}")
                                                }

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                showError(getString(R.string.error))
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                                        , {
                                            it.printStackTrace()
                                            showError(getString(R.string.error))
                                            dialog.dismiss()
                                        })
                                )

                                disposables?.add(
                                    hubConnection.invoke(
                                        Array<Table>::class.java,
                                        "getTables"
                                    ).subscribeOn(Schedulers.io()).subscribe({ tables ->
                                        synchronized(mSync) {
                                            Timber.e("decTableNo ${tables[0].decTableNo}")
                                            try {
                                                tableDao.insertAll(tables.asList())

                                                tables.forEach {
                                                    Timber.d("Inserted new table, ID: ${it.decId}")
                                                }

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                showError(getString(R.string.error))
                                                dialog.dismiss()
                                            }
                                        }
                                    }, {
                                        it.printStackTrace()
                                        showError(getString(R.string.error))
                                        dialog.dismiss()
                                    })
                                )


                                disposables?.add(
                                    hubConnection.invoke(
                                        Array<Option>::class.java,
                                        "getOptions"
                                    ).subscribeOn(Schedulers.io()).subscribe({ opts ->
                                        synchronized(mSync) {
                                            if (opts != null) {
                                                try {
                                                    Timber.e("optName ${opts[0].optName}")

                                                    optionDao.insertAll(opts.asList())

                                                    opts.forEach {
                                                        Timber.d("Inserted new opts, ID: ${it.optId}")
                                                    }

                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    }, {
                                        it.printStackTrace()
                                        showError(getString(R.string.error))
                                        dialog.dismiss()
                                    })
                                )

                                disposables?.add(
                                    hubConnection.invoke(Array<Meal>::class.java, "getMenu")
                                        .subscribeOn(Schedulers.io()).subscribe({ meals ->
                                            synchronized(mSync) {

                                                disposables?.add(
                                                    Completable.fromAction {
                                                        mealDao.insertAll(meals.asList())
                                                    }.observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeOn(Schedulers.io()).subscribe(
                                                            {
                                                                disposables?.add(
                                                                    hubConnection.invoke(
                                                                        Array<MealOptions>::class.java,
                                                                        "getMealOptions"
                                                                    ).subscribeOn(Schedulers.io()).subscribe({ mealOpts ->
                                                                        synchronized(mSync) {

                                                                            if (mealOpts != null) {
                                                                                try {
                                                                                    mealOptionsDao.insertAll(
                                                                                        mealOpts.asList()
                                                                                    )

                                                                                    mealOpts.forEach {
                                                                                        Timber.d("Inserted new mealOpts, ID: ${it.optId}")
                                                                                    }

                                                                                } catch (e: Exception) {
                                                                                    e.printStackTrace()
                                                                                }
                                                                            }
                                                                        }
                                                                    }, {
                                                                        it.printStackTrace()
                                                                        showError(getString(R.string.error))
                                                                        dialog.dismiss()
                                                                    })
                                                                )

                                                                meals.forEach { meal ->

                                                                    Timber.d("mealDaoInserted new meal, ID: ${meal.melId}")

                                                                    try {
                                                                        disposables?.add(
                                                                            hubConnection.invoke(
                                                                                String::class.java,
                                                                                "getImage",
                                                                                meal.melLogo
                                                                            ).subscribeOn(
                                                                                Schedulers.computation()
                                                                            )
                                                                                .subscribe(
                                                                                    { img ->
                                                                                        synchronized(
                                                                                            mSync
                                                                                        ) {
                                                                                            try {
                                                                                                Timber.i(
                                                                                                    "MEL_Logo ${meal.melLogo}"
                                                                                                )
                                                                                                val decodedImg =
                                                                                                    Base64.decode(
                                                                                                        img,
                                                                                                        Base64.DEFAULT
                                                                                                    )
                                                                                                meal.Image =
                                                                                                    decodedImg

                                                                                                mealDao.update(
                                                                                                    meal
                                                                                                )
                                                                                            } catch (e: Exception) {
                                                                                                Timber.e(
                                                                                                    "IMG: $img"
                                                                                                )
                                                                                                e.printStackTrace()
                                                                                               // showError(
                                                                                              //     getString(
                                                                                                     //   R.string.error
                                                                                                  //  )
                                                                                             //   )
                                                                                                dialog.dismiss()
                                                                                            }

                                                                                        }
                                                                                    },
                                                                                    {
                                                                                        it.printStackTrace()
                                                                                    //    showError(
                                                                                     //       getString(
                                                                                                R.string.error
                                                                                          //  )
                                                                                    //    )
                                                                                      //  dialog.dismiss()
                                                                                    })
                                                                        )
                                                                    } catch (e: Exception) {
                                                                      //  e.printStackTrace()
                                                                     //   showError(getString(R.string.error))
                                                                       // dialog.dismiss()
                                                                    }
                                                                }
                                                                dialog.dismiss()
                                                                activity?.window?.setSoftInputMode(
                                                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                                                                )
                                                                updateMenu()
                                                            },
                                                            {
                                                                it.printStackTrace()
                                                                showError(getString(R.string.error))
                                                                dialog.dismiss()
                                                            })
                                                )
                                            }
                                        }, {
                                            it.printStackTrace()
                                            showError(getString(R.string.error))
                                            dialog.dismiss()
                                        })
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showError(getString(R.string.error))
                                dialog.dismiss()
                            }
                        }
                    }, {
                        it.printStackTrace()
                        showError(getString(R.string.error))
                        dialog.dismiss()
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showError(getString(R.string.error))
            dialog.dismiss()
            GlobalScope.launch {
                try {
                    hubConnection.start().blockingAwait()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposables?.dispose()
    }

    companion object {
        private val mSync = Any()
    }

    private val swipeMenuCreator =
        SwipeMenuCreator { swipeLeftMenu, swipeRightMenu, position ->
            val width = 150

            val height = ViewGroup.LayoutParams.MATCH_PARENT

            val deleteItem = SwipeMenuItem(activity).setBackground(R.drawable.selector_red)
                .setImage(R.drawable.baseline_delete_24)
                .setText("حذف")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
            swipeRightMenu.addMenuItem(deleteItem)

            val addItem = SwipeMenuItem(activity).setBackground(R.drawable.selector_green)
                .setImage(R.drawable.baseline_tune_24)
                .setText("خيارات")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
            swipeRightMenu.addMenuItem(addItem)
        }


    private val mMenuItemClickListener = OnItemMenuClickListener { menuBridge, position ->
        menuBridge.closeMenu()

        val direction = menuBridge.direction
        val menuPosition = menuBridge.position

        if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {


            if (menuPosition == 1)
                try {

                    if (orders.value!![position].options.isNullOrEmpty()) {

                        GlobalScope.launch {
                            val mealOptions: MutableList<MealOptions>? =
                                mealOptionsDao.getOptions(orders.value!![position].ordD_MealID)

                            if (!mealOptions.isNullOrEmpty()) {

                                orders.value!![position].options = ArrayList()

                                mealOptions.forEach {

                                    orders.value!![position].options!!.add(optionDao.getOption(it.optId))
                                }

                                showOptions(position)
                            } else
                                showError("لا يوجد خيارات")
                        }
                    } else
                        showOptions(position)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            else if (menuPosition == 0)
                try {

                    SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("هل أنت متأكد؟")
                        .setConfirmText("نعم")
                        .setCancelText("لا")
                        .setConfirmClickListener {

                            it.dismissWithAnimation()

                            val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog_note, null)

                            val editText: EditText = inflate.findViewById(R.id.custom_edit)
                            editText.setTextColor(Color.BLACK)
                            editText.setLines(10)
                            editText.setBackgroundResource(R.drawable.edittext_bg)

                            val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("سبب الحذف").setCustomView(inflate).setConfirmText("تأكيد")
                                .setConfirmClickListener { d ->


                                    CommonUtils.hideKeyboard(context, editText)
                                    hubConnection.send(
                                        "deleteMeal", orders.value!![position].ordD_Serial, editText.text.toString()
                                    )
                                    d.dismissWithAnimation()

                                    orders.value!!.removeAt(position)

                                    orders.postValue(orders.value)
                                }

                            dialog.setCancelable(false)
                            dialog.show()

                        }.setCancelClickListener {
                            it.cancel()
                        }.show()


                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }
    }

    private fun showOptions(position: Int) {
        val fragment = OptionsDialogFragment()

        fragment.setData(orders.value!![position].options!!, activity!!)

        fragment.show((activity as MainActivity).supportFragmentManager, "dialog")
    }
}