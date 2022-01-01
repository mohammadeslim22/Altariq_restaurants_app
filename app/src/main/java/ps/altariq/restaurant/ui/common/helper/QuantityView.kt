package ps.altariq.restaurant.ui.common.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import ps.altariq.restaurant.R
import ps.altariq.restaurant.ui.main.adapter.OrderAdapter
import timber.log.Timber

class QuantityView : LinearLayout, View.OnClickListener {

    private var quantityBackground: Drawable? = null
    private var addButtonBackground: Drawable? = null
    private var removeButtonBackground: Drawable? = null

    private var quantity: Int = 0
    var maxQuantity = Integer.MAX_VALUE
    var minQuantity = Integer.MAX_VALUE

    private var quantityTextColor: Int = 0
    private var addButtonTextColor: Int = 0
    private var removeButtonTextColor: Int = 0

    private var mButtonAdd: Button? = null
    private var mButtonRemove: Button? = null
    private var mTextViewQuantity: TextView? = null


    var onQuantityChangeListener: OnQuantityChangeListener? = null
    private var mTextViewClickListener: View.OnClickListener? = null


    interface OnQuantityChangeListener {
        fun onQuantityChanged(oldQuantity: Int, newQuantity: Int, programmatically: Boolean)

        fun onLimitReached()
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {

        addButtonBackground = ContextCompat.getDrawable(context, R.color.material_green_A700)


        addButtonTextColor = Color.WHITE

        removeButtonBackground = ContextCompat.getDrawable(context, R.color.material_red_A400)

        removeButtonTextColor = Color.WHITE

        quantity = 1
        maxQuantity = Integer.MAX_VALUE
        minQuantity = 1

        quantityTextColor = Color.BLACK
        quantityBackground = ContextCompat.getDrawable(context, R.drawable.qv_bg_selector)

        mButtonAdd = Button(context, null, android.R.attr
                .borderlessButtonStyle)
        mButtonAdd!!.gravity = Gravity.CENTER
        mButtonAdd!!.minimumHeight = 0
        mButtonAdd!!.minimumWidth = 0
        mButtonAdd!!.minHeight = 0
        mButtonAdd!!.minWidth = 0
        mButtonAdd!!.minEms = 1


        mButtonAdd!!.elevation = 0f
        mButtonAdd!!.text = "+"

        setAddButtonBackground(addButtonBackground)
        setAddButtonTextColor(addButtonTextColor)

        mButtonRemove = Button(context, null, android.R.attr
                .borderlessButtonStyle)
        mButtonRemove!!.gravity = Gravity.CENTER
        mButtonRemove!!.minimumHeight = 0
        mButtonRemove!!.minimumWidth = 0
        mButtonRemove!!.minHeight = 0
        mButtonRemove!!.minWidth = 0
        mButtonRemove!!.minEms = 1

        mButtonRemove!!.elevation = 0f
        mButtonRemove!!.text = "-"
        setRemoveButtonBackground(removeButtonBackground)
        setRemoveButtonTextColor(removeButtonTextColor)

        mTextViewQuantity = TextView(context)
        mTextViewQuantity!!.gravity = Gravity.CENTER
        setQuantityTextColor(quantityTextColor)
        setQuantity(quantity)
        setQuantityBackground(quantityBackground)

        mTextViewQuantity!!.setEms(2)
        mTextViewQuantity!!.textSize = 16f
        mTextViewQuantity!!.typeface = Typeface.DEFAULT_BOLD
        orientation = LinearLayout.HORIZONTAL

        addView(mButtonRemove, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        addView(mTextViewQuantity, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        addView(mButtonAdd, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        mButtonAdd!!.setOnClickListener(this)
        mButtonRemove!!.setOnClickListener(this)
        mTextViewQuantity!!.setOnClickListener(this)
    }


    fun setTextChangedListener(tw: OrderAdapter.MyCustomEditTextListener?) {
        mTextViewQuantity!!.addTextChangedListener(tw)
    }

    fun setQuantityClickListener(ocl: View.OnClickListener) {
        mTextViewClickListener = ocl
    }

    override fun onClick(v: View) {
        if (v === mButtonAdd) {
            if (quantity + 1 > maxQuantity) {
                if (onQuantityChangeListener != null) onQuantityChangeListener!!.onLimitReached()
            } else {
                val oldQty = quantity
                quantity += 1
                mTextViewQuantity!!.text = quantity.toString()

            }
        } else if (v === mButtonRemove) {
            if (quantity - 1 < minQuantity) {
                if (onQuantityChangeListener != null) onQuantityChangeListener!!.onLimitReached()
            } else {

                val oldQty = quantity
                quantity -= 1
                mTextViewQuantity!!.text = quantity.toString()

            }
        } else if (v === mTextViewQuantity) {

            if (mTextViewClickListener != null) {
                mTextViewClickListener!!.onClick(v)
            }


            val inflate: View = LayoutInflater.from(context).inflate(R.layout.dialog, null)

            val editText: EditText = inflate.findViewById(R.id.custom_edit)

            editText.setText(quantity.toString())
            editText.setTextColor(Color.BLACK)
            editText.setBackgroundResource(R.drawable.edittext_bg)

            editText.selectAll()

            val dialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("الكمية").setCustomView(inflate).setConfirmText("حفظ")
                    .setConfirmClickListener {

                        val newQuantity = editText.text.toString()
                        if (isValidNumber(newQuantity)) {
                            val intNewQuantity = newQuantity.toInt()
                            Timber.d("newQuantity $intNewQuantity max $maxQuantity")
                            if (intNewQuantity > maxQuantity) {

                            } else if (intNewQuantity < minQuantity) {
                            } else {
                                setQuantity(intNewQuantity)
                            }

                            hideKeyboard(editText)
                            it.dismiss()
                        } else {
                            Toast.makeText(context, "Enter valid number", Toast.LENGTH_LONG).show()
                        }

                    }

            dialog.setCancelable(false)

            dialog.show()

        }
    }

    fun hideKeyboard(view: View) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun getQuantityBackground(): Drawable? {
        return quantityBackground
    }

    fun setQuantityBackground(quantityBackground: Drawable?) {
        this.quantityBackground = quantityBackground
        mTextViewQuantity!!.background = quantityBackground

    }

    fun getAddButtonBackground(): Drawable? {
        return addButtonBackground
    }

    fun setAddButtonBackground(addButtonBackground: Drawable?) {
        this.addButtonBackground = addButtonBackground
        mButtonAdd!!.background = addButtonBackground

    }

    fun getRemoveButtonBackground(): Drawable? {
        return removeButtonBackground
    }

    fun setRemoveButtonBackground(removeButtonBackground: Drawable?) {
        this.removeButtonBackground = removeButtonBackground
        mButtonRemove!!.background = removeButtonBackground

    }

    fun getQuantity(): Int {
        return quantity
    }

    fun setQuantity(newQuantity: Int) {
        var newQuantity = newQuantity
        var limitReached = false

        if (newQuantity > maxQuantity) {
            newQuantity = maxQuantity
            limitReached = true
        }
        if (newQuantity < minQuantity) {
            newQuantity = minQuantity
            limitReached = true
        }
        if (!limitReached) {
            //            if (onQuantityChangeListener != null) {
            //                onQuantityChangeListener.onQuantityChanged(quantity, newQuantity, true);
            //            }
            this.quantity = newQuantity
            mTextViewQuantity!!.text = this.quantity.toString()
        } else {
            if (onQuantityChangeListener != null) onQuantityChangeListener!!.onLimitReached()
        }
    }

    fun getQuantityTextColor(): Int {
        return quantityTextColor
    }

    fun setQuantityTextColor(quantityTextColor: Int) {
        this.quantityTextColor = quantityTextColor
        mTextViewQuantity!!.setTextColor(quantityTextColor)
    }

    fun setQuantityTextColorRes(quantityTextColorRes: Int) {
        this.quantityTextColor = ContextCompat.getColor(context, quantityTextColorRes)
        mTextViewQuantity!!.setTextColor(quantityTextColor)
    }

    fun getAddButtonTextColor(): Int {
        return addButtonTextColor
    }

    fun setAddButtonTextColor(addButtonTextColor: Int) {
        this.addButtonTextColor = addButtonTextColor
        mButtonAdd!!.setTextColor(addButtonTextColor)
    }

    fun setAddButtonTextColorRes(addButtonTextColorRes: Int) {
        this.addButtonTextColor = ContextCompat.getColor(context, addButtonTextColorRes)
        mButtonAdd!!.setTextColor(addButtonTextColor)
    }

    fun getRemoveButtonTextColor(): Int {
        return removeButtonTextColor
    }

    fun setRemoveButtonTextColor(removeButtonTextColor: Int) {
        this.removeButtonTextColor = removeButtonTextColor
        mButtonRemove!!.setTextColor(removeButtonTextColor)
    }

    fun setRemoveButtonTextColorRes(removeButtonTextColorRes: Int) {
        this.removeButtonTextColor = ContextCompat.getColor(context, removeButtonTextColorRes)
        mButtonRemove!!.setTextColor(removeButtonTextColor)
    }

    private fun dpFromPx(px: Float): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

    private fun pxFromDp(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun isValidNumber(string: String): Boolean {
        try {
            return Integer.parseInt(string) <= Integer.MAX_VALUE
        } catch (e: Exception) {
            return false
        }

    }

}