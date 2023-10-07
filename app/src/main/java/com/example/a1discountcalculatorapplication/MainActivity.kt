package com.example.a1discountcalculatorapplication

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.example.a1discountcalculatorapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi

class AboutFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about, container, false)
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var prevFocusView: EditText

    @SuppressLint("UseSwitchCompatOrMaterialCode", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup UI
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        b.ettPrice.showSoftInputOnFocus = false
        b.ettTaxRate.showSoftInputOnFocus = false
        b.ettDiscountPercent.showSoftInputOnFocus = false
        b.ettDiscountFixed.showSoftInputOnFocus = false

        // set default focus
        b.ettPrice.requestFocus()
        // Set focused id
        prevFocusView = focus()

        // Set animation
        val enterAnimation = AnimationUtils.makeInAnimation(this, true)
        val exitAnimation = AnimationUtils.makeOutAnimation(this, true)
        enterAnimation.duration = 250
        exitAnimation.duration = 250


        // On click
        b.btnDown.setOnClickListener { focusRow(false) }
        b.btnUp.setOnClickListener { focusRow(true) }
        b.btnBack.setOnClickListener {
            val length = focus().length()
            if (length > 0) {
                val start = focus().selectionStart
                val end = focus().selectionEnd
                if (start > 0) {
                    if (end - start > 0) {
                        focus().text.delete(start, end)
                    } else {
                        focus().text.delete(start - 1, start)
                    }
                }
            }
        }
        b.btnAllClear.setOnClickListener {
            focus().text = null
            focus().error = null
        }
        b.tvResult.setOnClickListener {
            val clipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", b.tvResult.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(
                this, "The final price has been copied to your clipboard.", Toast.LENGTH_SHORT
            ).show()
        }

        // On click for toggle Tax; Percent; Coupon
        b.switchT.setOnClickListener {
            val cont = b.resultContainer
            if (b.switchT.isChecked) {
                // Add
                b.row2.startAnimation(enterAnimation)
                cont.addView(b.row2)
                if (cont.childCount > 3) {
                    val temp1 = cont.getChildAt(3) as ViewGroup
                    val temp2 = temp1.getChildAt(0)
                    if (temp2 is SwitchCompat) {
                        temp2.isEnabled = true
                    }
                }
            } else {
                // Remove
                for (x in 3..cont.childCount) {
                    if (cont.getChildAt(x) == b.row2) {
                        // Focus previous row
                        var temp = cont.getChildAt(x) as ViewGroup

                        if (temp.getChildAt(1).hasFocus()) {
                            temp = cont.getChildAt(x - 1) as ViewGroup
                            temp.getChildAt(1).requestFocus()
                            temp = cont.getChildAt(x) as ViewGroup
                        }
                        // Remove the row
                        val temp2: SwitchCompat = temp.getChildAt(0) as SwitchCompat
                        temp2.setTextColor(getColor(R.color.dark_blue))
                        temp2.isChecked = false
                        temp2.isEnabled = true
                        val temp3 = temp.getChildAt(1) as EditText
                        temp3.error = null
                        temp3.text = null
                        b.row2.setBackgroundColor(getColor(R.color.transparent))
                        b.row2.startAnimation(exitAnimation)
                        cont.removeView(b.row2)
                        break
                    }
                }
            }
            // Check if 2nd row is tax or discount then switch it on and disable it
            if (cont.childCount > 3) {
                val temp1 = cont.getChildAt(3) as ViewGroup
                val temp2 = temp1.getChildAt(0)
                if (temp2 is SwitchCompat) {
                    temp2.isChecked = true
                    temp2.isEnabled = false
                }
            }
        }
        b.switchDp.setOnClickListener {
            val cont = b.resultContainer
            if (b.switchDp.isChecked) {
                // Add
                b.row3.startAnimation(enterAnimation)
                cont.addView(b.row3)
                if (cont.childCount > 3) {
                    val temp1 = cont.getChildAt(3) as ViewGroup
                    val temp2 = temp1.getChildAt(0)
                    if (temp2 is SwitchCompat) {
                        temp2.isEnabled = true
                    }
                }
            } else {
                // Remove
                for (x in 3..cont.childCount) {
                    if (cont.getChildAt(x) == b.row3) {
                        // Focus previous row
                        var temp = cont.getChildAt(x) as ViewGroup
                        if (temp.getChildAt(1).hasFocus()) {
                            temp = cont.getChildAt(x - 1) as ViewGroup
                            temp.getChildAt(1).requestFocus()
                            temp = cont.getChildAt(x) as ViewGroup
                        }

                        // Remove the row
                        val temp2 = temp.getChildAt(0) as SwitchCompat
                        temp2.setTextColor(getColor(R.color.dark_blue))
                        temp2.isChecked = false
                        temp2.isEnabled = true
                        val temp3 = temp.getChildAt(1) as EditText
                        temp3.error = null
                        temp3.text = null
                        b.row3.setBackgroundColor(getColor(R.color.transparent))
                        b.row3.startAnimation(exitAnimation)
                        cont.removeView(b.row3)
                        break
                    }
                }
            }
            // Check if 2nd row is tax or discount then switch it on and disable it
            if (cont.childCount > 3) {
                val temp1 = cont.getChildAt(3) as ViewGroup
                val temp2 = temp1.getChildAt(0)
                if (temp2 is SwitchCompat) {
                    temp2.isChecked = true
                    temp2.isEnabled = false
                }
            }
        }
        b.switchDf.setOnClickListener {
            val cont = b.resultContainer
            if (b.switchDf.isChecked) {
                // Add
                b.row4.startAnimation(enterAnimation)
                cont.addView(b.row4)
                if (cont.childCount > 3) {
                    val temp1 = cont.getChildAt(3) as ViewGroup
                    val temp2 = temp1.getChildAt(0)
                    if (temp2 is SwitchCompat) {
                        temp2.isEnabled = true
                    }
                }
            } else {
                // Remove
                for (x in 3..cont.childCount) {
                    if (cont.getChildAt(x) == b.row4) {
                        // Focus previous row
                        var temp = cont.getChildAt(x) as ViewGroup
                        if (temp.getChildAt(1).hasFocus()) {
                            temp = cont.getChildAt(x - 1) as ViewGroup
                            temp.getChildAt(1).requestFocus()
                            temp = cont.getChildAt(x) as ViewGroup
                        }

                        // Remove the row
                        val temp2 = temp.getChildAt(0) as TextView
                        temp2.setTextColor(getColor(R.color.dark_blue))
                        val temp3 = temp.getChildAt(1) as EditText
                        temp3.error = null
                        temp3.text = null
                        b.row4.setBackgroundColor(getColor(R.color.transparent))
                        b.row4.startAnimation(exitAnimation)
                        cont.removeView(b.row4)
                        break
                    }
                }
            }
            // Check if 2nd row is tax or discount then switch it on and disable it
            if (cont.childCount > 3) {
                val temp1 = cont.getChildAt(3) as ViewGroup
                val temp2 = temp1.getChildAt(0)
                if (temp2 is SwitchCompat) {
                    temp2.isChecked = true
                    temp2.isEnabled = false
                }
            }
        }

        // On checked change for Tax; Percent
        b.switchTaxRate.setOnClickListener {
            if (b.switchTaxRate.isChecked) {
                Toast.makeText(
                    this,
                    "The tax rate will based on the initial price.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "The tax rate will based on the calculated price.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        b.switchPercent.setOnClickListener {
            if (b.switchPercent.isChecked) {
                Toast.makeText(
                    this,
                    "The discount percent will based on the initial price.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "The discount percent will based on the calculated price.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // On text change
        b.ettPrice.doOnTextChanged { _, _, _, _ ->
            if (DiscountCalculator(
                    b.ettPrice,
                    b.ettTaxRate,
                    b.ettDiscountPercent,
                    b.ettDiscountFixed
                ).calculateResult() > 0
            ) {
                b.tvResult.text = String.format(
                    "%.2f",
                    DiscountCalculator(
                        b.ettPrice,
                        b.ettTaxRate,
                        b.ettDiscountPercent,
                        b.ettDiscountFixed
                    ).calculateResult().toString()
                        .toDouble()
                )
            } else {
                b.tvResult.text = "0.00"
            }
        }
//        b.ettTaxRate.addTextChangedListener()
//        b.ettDiscountPercent.addTextChangedListener()
//        b.ettDiscountFixed.addTextChangedListener()

        // On focus change
        b.ettPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
        }
        b.ettTaxRate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
        }
        b.ettDiscountPercent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
        }
        b.ettDiscountFixed.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Landscape
            // TODO: Set background when orientation change
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        b.toolbar.title = "Discount Calculator"
        b.toolbar.setBackgroundColor(getColor(R.color.faded_blue))
        menu.findItem(R.id.menu_flip).isEnabled = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> true
            Configuration.ORIENTATION_PORTRAIT -> false
            else -> false
        }

        return super.onCreateOptionsMenu(menu)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset -> {
                b.row1.setBackgroundColor(getColor(R.color.transparent))
                b.row2.setBackgroundColor(getColor(R.color.transparent))
                b.row3.setBackgroundColor(getColor(R.color.transparent))
                b.row4.setBackgroundColor(getColor(R.color.transparent))

                b.tvPrice.setTextColor(getColor(R.color.dark_blue))
                b.switchTaxRate.setTextColor(getColor(R.color.dark_blue))
                b.switchPercent.setTextColor(getColor(R.color.dark_blue))
                b.tvFixed.setTextColor(getColor(R.color.dark_blue))

                switchRowFocus()

                b.ettPrice.text = null
                b.ettTaxRate.text = null
                b.ettDiscountPercent.text = null
                b.ettDiscountFixed.text = null

                b.ettPrice.error = null
                b.ettTaxRate.error = null
                b.ettDiscountPercent.error = null
                b.ettDiscountFixed.error = null
            }

            R.id.menu_flip -> {
                // Flip the screen
                prevFocusView = currentFocus as EditText
                val container = b.container as ViewGroup
                val v1 = container.getChildAt(0)
                val v2 = container.getChildAt(1)
                val v3 = container.getChildAt(2)

                val exitAnimation = AnimationUtils.makeOutAnimation(this, true)
                val enterAnimation = AnimationUtils.makeInAnimation(this, true)
                exitAnimation.duration = 250
                enterAnimation.duration = 250

                exitAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        // Do nothing
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        // Remove the views from the container
                        container.removeView(v1)
                        container.removeView(v2)
                        container.removeView(v3)
                        container.addView(v3)
                        container.addView(v2)
                        container.addView(v1)
                        container.startAnimation(enterAnimation)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        // Do nothing
                    }
                })
                    enterAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            // Do nothing
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            // Set focus
                            prevFocusView.requestFocus()
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                            // Do nothing
                        }
                    })
                container.startAnimation(exitAnimation)


            }

            R.id.menu_about -> {
                // Show the fragment
                AboutFragment().show(supportFragmentManager, "about_fragment")

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun numKeyAction(view: View) {
        val defaultDecimalFormat = Regex("^[0-9]*.?[0-9]{0,2}$")
        if (view is Button) {
            val start = focus().selectionStart
            val end = focus().selectionEnd
            if (end - start > 0) {
                focus().text.delete(start, end)
            }

            if (focus().text.isEmpty()) {
                if (view.text.equals(".")) {
                    focus().setText("0.")
                    focus().setSelection(focus().length())
                    return
                }
            }
            var temp: String = focus().text.toString()
            temp = temp.substring(0, start) + view.text + temp.substring(start)

            if (temp.matches(defaultDecimalFormat)) {
                focus().text.insert(start, view.text)
            }
        }
    }

    private fun focusRow(previous: Boolean) {
        if (previous) {
            // Focus previous row
            val cont = b.resultContainer
            var childOfCont: ViewGroup
            for (x in 2..b.resultContainer.childCount) {
                childOfCont = cont.getChildAt(x) as ViewGroup
                if (childOfCont.getChildAt(1).hasFocus()) {
                    if (x == 2) {
                        return
                    } else {
                        // Focus previous row
                        childOfCont = cont.getChildAt(x - 1) as ViewGroup
                        childOfCont.getChildAt(1).requestFocus()
                        return
                    }
                }
            }
        } else {
            // Focus next row
            val cont = b.resultContainer
            var childOfCont: ViewGroup
            for (x in 2..b.resultContainer.childCount) {
                childOfCont = cont.getChildAt(x) as ViewGroup
                if (childOfCont.getChildAt(1).hasFocus()) {
                    if (x == b.resultContainer.childCount - 1) {
                        return
                    } else {
                        // Focus previous row
                        childOfCont = cont.getChildAt(x + 1) as ViewGroup
                        childOfCont.getChildAt(1).requestFocus()
                        return
                    }
                }
            }
        }
        // Update current focus variable
        prevFocusView = currentFocus as EditText
    }

    @SuppressLint("SetTextI18n")
    private fun switchRowFocus() {
        var prevRow: View = when (prevFocusView) {
            b.ettPrice -> b.row1
            b.ettTaxRate -> b.row2
            b.ettDiscountPercent -> b.row3
            else -> b.row4
        }
        var prevLabel = when (prevFocusView) {
            b.ettPrice -> b.tvPrice
            b.ettTaxRate -> b.switchTaxRate
            b.ettDiscountPercent -> b.switchPercent
            else -> b.tvFixed
        }

        // Process Previous Row
        if (prevFocusView.text.isNotEmpty()) {
            // Set Tax and Percent Off Limit to 100
            if ((prevFocusView == b.ettTaxRate) or (prevFocusView == b.ettDiscountPercent)) {
                if (prevFocusView.text.toString().toDouble() > 100) {
                    prevFocusView.setText("100.00")
                }
            }
            // Format the value
            prevFocusView.setText(
                String.format(
                    "%.2f",
                    prevFocusView.text.toString().toDouble()
                )
            )

            // Set the colors
            prevLabel.setTextColor(getColor(R.color.dark_green))
            prevFocusView.setTextColor(getColor(R.color.dark_green))
            prevRow.setBackgroundColor(getColor(R.color.light_green))
        } else {
            // Change previous background to pending
            val message: String = when (prevFocusView) {
                b.ettPrice -> "The Price can not be empty."
                b.ettTaxRate -> "The tax rate can not be empty."
                b.ettDiscountPercent -> "The discount percent can not be empty."
                else -> "The discount fixed amount can not be empty."
            }
            if (message.isNotEmpty()) {
                // Set the colors
                prevLabel.setTextColor(getColor(R.color.dark_red))
                prevFocusView.setTextColor(getColor(R.color.dark_red))
                prevRow.setBackgroundColor(getColor(R.color.faded_red))
                prevFocusView.error = message
            } else {
                prevFocusView.error = null
                prevLabel.setTextColor(getColor(R.color.dark_blue))
                prevFocusView.setTextColor(getColor(R.color.dark_blue))
                prevRow.setBackgroundColor(getColor(R.color.transparent))
            }
        }

        // Process Current Row
        prevFocusView = focus()
        prevFocusView.setSelection(prevFocusView.text.length)
        prevRow = when (prevFocusView) {
            b.ettPrice -> b.row1
            b.ettTaxRate -> b.row2
            b.ettDiscountPercent -> b.row3
            else -> b.row4
        }
        prevLabel = when (prevFocusView) {
            b.ettPrice -> b.tvPrice
            b.ettTaxRate -> b.switchTaxRate
            b.ettDiscountPercent -> b.switchPercent
            else -> b.tvFixed
        }
        // Highlight the row
        prevLabel.setTextColor(getColor(R.color.white))
        prevFocusView.setTextColor(getColor(R.color.white))
        prevFocusView.error = null
        prevRow.setBackgroundColor(getColor(R.color.dark_blue))
    }

    private fun focus(): EditText {
        return currentFocus as EditText
    }

}

class DiscountCalculator(price: EditText, tax: EditText, percent: EditText, fixed: EditText) {
    private val valPrice =
        if (price.text.isNotEmpty()) price.text.toString().toDouble() else 0.0
    private val valTax = if (tax.text.isNotEmpty()) tax.text.toString().toDouble() else 0.0
    private val valPercent =
        if (percent.text.isNotEmpty()) percent.text.toString().toDouble() else 0.0
    private val valFixed =
        if (fixed.text.isNotEmpty()) fixed.text.toString().toDouble() else 0.0
    private var result: Double = 0.0

    fun calculateResult(): Double {
        // TODO: Formula A - Discount Percent > Tax Rate > Discount Fixed (DONE)
        // TODO: Formula B - Discount Percent > Discount Fixed > Tax Rate
        // TODO: Formula C - Discount Fixed > Discount Percent > Tax Rate
        // TODO: Formula D - Tax Rate > Discount Percent > Discount Fixed
        // TODO: Formula E - Tax Rate > Discount Fixed > Discount Percent
        // TODO: Formula F - Discount Fixed > Tax Rate > Discount Percent
        val percentAmount = valPrice * (valPercent / 100)
        val taxAmount = (valPrice - percentAmount) * (valTax / 100)
        return valPrice - percentAmount + taxAmount - valFixed
    }
}

class UIState() {
//    TODO("Try to remember UI state for Row1 Row2 Row3 and Row4")
}
