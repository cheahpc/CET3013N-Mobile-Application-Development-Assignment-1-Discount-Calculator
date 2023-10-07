package com.example.a1discountcalculatorapplication

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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.a1discountcalculatorapplication.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Refresh UI
        uiInit()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(baseContext, "Landscape Mode", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(baseContext, "Portrait Mode", Toast.LENGTH_SHORT).show()
        }

        setContentView(R.layout.activity_main)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        // Refresh the ui
        uiInit()

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

    private fun calculate() {
        val rCont = b.resultContainer
        var result = 0.00
        var fString = ""
        val vPrice =
            if (b.ettPrice.text.isNotEmpty()) b.ettPrice.text.toString().toDouble() else 0.0
        val vTax =
            if (b.ettTaxRate.text.isNotEmpty()) (b.ettTaxRate.text.toString()
                .toDouble() / 100) else 0.0
        val vDP =
            if (b.ettDiscountPercent.text.isNotEmpty()) (b.ettDiscountPercent.text.toString()
                .toDouble() / 100) else 0.0
        val vDF =
            if (b.ettDiscountFixed.text.isNotEmpty()) b.ettDiscountFixed.text.toString()
                .toDouble() else 0.0
        val x = DiscountCalculator(vPrice, vTax, vDP, vDF)

        when (rCont.childCount) {
            3 -> {
                // Price
                fString = getString(R.string.f1)
                result = x.calculateResult(1)
            }

            4 -> {
                // Price [A1/B1/C]
                when (rCont.getChildAt(3) as ViewGroup) {
                    // Price A
                    b.row2 -> {
                        // Price [A1]
                        fString = getString(R.string.f2)
                        result = x.calculateResult(2)
                    }

                    // Price B
                    b.row3 -> {
                        // Price [B1]
                        fString = getString(R.string.f3)
                        result = x.calculateResult(3)
                    }

                    // Price C
                    b.row4 -> {
                        // Price [C]
                        fString = getString(R.string.f4)
                        result = x.calculateResult(4)
                    }

                    // Error
                    else -> {
                        // Default - Error
                        fString = getString(R.string.error)
                        result = 0.00
                    }
                }
            }

            5 -> {
                // Price [A/B/C] [A/B/C]
                val childRow1 = rCont.getChildAt(3) as ViewGroup
                val childRow2 = rCont.getChildAt(4) as ViewGroup
                when (childRow1) {
                    // Price Ax
                    b.row2 -> {
                        if (childRow2 == b.row3) {
                            // Price [A] [B] : 2
                            val child = childRow2.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [A] [B0]
                                fString = getString(R.string.f5)
                                result = x.calculateResult(5)
                            } else {
                                // Price [A] [B1]
                                fString = getString(R.string.f6)
                                result = x.calculateResult(6)
                            }
                        } else {
                            // Price [A] [C]
                            fString = getString(R.string.f7)
                            result = x.calculateResult(7)
                        }
                    }

                    // Price Bx
                    b.row3 -> {
                        if (childRow2 == b.row2) {
                            // Price [B] [A]
                            val child = childRow2.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [B] [A0]
                                fString = getString(R.string.f8)
                                result = x.calculateResult(8)
                            } else {
                                // Price [B] [A1] = Price [A] [B1] -- 6
                                fString = getString(R.string.f9)

                                result = x.calculateResult(6)
                            }
                        } else {
                            // Price [B] [C]
                            fString = getString(R.string.f10)
                            result = x.calculateResult(9)
                        }
                    }

                    // Price Cx
                    b.row4 -> {
                        val child = childRow2.getChildAt(0) as SwitchCompat
                        if (childRow2 == b.row2) {
                            if (!child.isChecked) {
                                // Price [C] [A0]
                                fString = getString(R.string.f11)
                                result = x.calculateResult(10)
                            } else {
                                // Price [C] [A1] = Price [A] [C] -- 7
                                fString = getString(R.string.f12)
                                result = x.calculateResult(7)
                            }
                        } else {
                            // Price [C] [B]
                            if (!child.isChecked) {
                                // Price [C] [B0]
                                fString = getString(R.string.f13)
                                result = x.calculateResult(11)
                            } else {
                                // Price [C] [B1] = Price [B] [C] -- 9
                                fString = getString(R.string.f14)
                                result = x.calculateResult(9)
                            }
                        }
                    }

                    // Error
                    else -> {
                        // Default - Error
                        fString = getString(R.string.error)
                        result = 0.00
                    }
                }

            }

            6 -> {
                // Price [A/B/C] [A/B/C] [A/B/C]
                val childRow1 = rCont.getChildAt(3) as ViewGroup
                val childRow2 = rCont.getChildAt(4) as ViewGroup
                val childRow3 = rCont.getChildAt(5) as ViewGroup
                when (childRow1) {
                    // Price Axx
                    b.row2 -> {
                        if (childRow2 == b.row3) {
                            val child = childRow2.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [A] [B0] [C]
                                fString = getString(R.string.f15)
                                result = x.calculateResult(12)

                            } else {
                                // Price [A] [B1] [C]
                                fString = getString(R.string.f16)
                                result = x.calculateResult(13)
                            }
                        } else {
                            val child = childRow3.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [A] [C] [B0]
                                fString = getString(R.string.f17)
                                result = x.calculateResult(14)
                            } else {
                                // Price [A] [C] [B1] = Price [A] [B1] [C] -- 13
                                fString = getString(R.string.f18)
                                result = x.calculateResult(13)
                            }
                        }
                    }

                    // Price Bxx
                    b.row3 -> {
                        if (childRow2 == b.row2) {
                            val child = childRow2.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [B] [A0] [C]
                                fString = getString(R.string.f19)
                                result = x.calculateResult(15)
                            } else {
                                // Price [B] [A1] [C] = Price [A] [B1] [C] -- 13
                                fString = getString(R.string.f20)
                                result = x.calculateResult(13)
                            }

                        } else {
                            val child = childRow3.getChildAt(0) as SwitchCompat
                            if (!child.isChecked) {
                                // Price [B] [C] [A0]
                                fString = getString(R.string.f21)
                                result = x.calculateResult(16)
                            } else {
                                // Price [B] [C] [A1] = Price [A] [B1] [C] -- 13
                                fString = getString(R.string.f22)
                                result = x.calculateResult(13)
                            }
                        }
                    }

                    // Price Cxx
                    b.row4 -> {
                        val child1 = childRow2.getChildAt(0) as SwitchCompat
                        val child2 = childRow3.getChildAt(0) as SwitchCompat
                        if (childRow2 == b.row2) {
                            if (!child1.isChecked) {
                                if (!child2.isChecked) {
                                    // Price [C] [A0] [B0]
                                    fString = getString(R.string.f23)
                                    result = x.calculateResult(17)
                                } else {
                                    // Price [C] [A0] [B1]
                                    fString = getString(R.string.f24)
                                    result = x.calculateResult(18)
                                }
                            } else {
                                if (!child2.isChecked) {
                                    // Price [C] [A1] [B0]
                                    fString = getString(R.string.f25)
                                    result = x.calculateResult(19)
                                } else {
                                    // Price [C] [A1] [B1]
                                    fString = getString(R.string.f26)
                                    result = x.calculateResult(13)
                                }
                            }
                        } else {
                            if (!child1.isChecked) {
                                if (!child2.isChecked) {
                                    // Price [C] [B0] [A0]
                                    fString = getString(R.string.f27)
                                    result = x.calculateResult(20)
                                } else {
                                    // Price [C] [B0] [A1]
                                    fString = getString(R.string.f28)
                                    result = x.calculateResult(21)
                                }
                            } else {
                                if (!child2.isChecked) {
                                    // Price [C] [B1] [A0]
                                    fString = getString(R.string.f29)
                                    result = x.calculateResult(22)
                                } else {
                                    // Price [C] [B1] [A1]
                                    fString = getString(R.string.f30)
                                    result = x.calculateResult(13)
                                }
                            }
                        }
                    }

                    // Error
                    else -> {
                        // Default - Error
                        fString = getString(R.string.error)
                        result = 0.00
                    }
                }
            }
        }

        // Update Result and UI

        result = BigDecimal(result).setScale(2, RoundingMode.HALF_UP).toDouble()
        b.tvResult.text = result.toString()
        b.tvFormula.text = fString
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
                    prevFocusView.setText(getString(R.string.max_value))
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
        if (currentFocus != null){
            return currentFocus as EditText
        }
        b.ettPrice.requestFocus()
        return b.ettPrice
    }

    private fun uiInit(){
        setContentView(R.layout.activity_main)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        // Setup UI
        setSupportActionBar(b.toolbar)
        b.ettPrice.showSoftInputOnFocus = false
        b.ettTaxRate.showSoftInputOnFocus = false
        b.ettDiscountPercent.showSoftInputOnFocus = false
        b.ettDiscountFixed.showSoftInputOnFocus = false

        // set default focus
        b.ettPrice.requestFocus()
        // Set focused id
        prevFocusView = focus()
        //Refresh UI

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
            calculate()
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
            calculate()
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
            calculate()
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
            calculate()
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
            calculate()
        }

        // On text change
        b.ettPrice.doAfterTextChanged { calculate() }
        b.ettTaxRate.doAfterTextChanged { calculate() }
        b.ettDiscountPercent.doAfterTextChanged { calculate() }
        b.ettDiscountFixed.doAfterTextChanged { calculate() }

        // On focus change
        b.ettPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
            calculate()
        }
        b.ettTaxRate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
            calculate()

        }
        b.ettDiscountPercent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
            calculate()
        }
        b.ettDiscountFixed.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                switchRowFocus()
            }
            calculate()
        }
    }

    @Suppress("unused")
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
}

class DiscountCalculator(
    private var p: Double,
    private var t: Double = 0.0,
    private var dp: Double = 0.0,
    private var c: Double = 0.0
) {
    private var result: Double = 0.0
    fun calculateResult(formula: Int): Double {
        when (formula) {
            1 -> {
                // Price
                result = p
            }
            // 1 Var
            2 -> {
                // Price [A1]
                result = p + (p * t)
            }

            3 -> {
                // Price [B1]
                result = p - (p * dp)
            }

            4 -> {
                // Price [C]
                result = p - c
            }

            // 2 Var A
            5 -> {
                // Price [A] [B0]
                result = p + (p * t) - ((p + (p * t)) * dp)
            }

            6 -> {
                // Price [A] [B1]
                result = p + (p * t) - (p * dp)
            }

            7 -> {
                // Price [A] [C]
                result = p + (p * t) - c
            }

            // 2 Var B
            8 -> {
                // Price [B] [A0]
                result = p - (p * dp) + ((p - (p * dp)) * t)
            }

            9 -> { // Price [B] [A1] = Price [A] [B1] -- 6
                // Price [B] [C]
                result = p - (p * dp) - c
            }

            // 2 Var C
            10 -> {
                // Price [C] [A0]
                result = p - c + ((p - c) * t)
            }

            11 -> { // Price [C] [A1] = Price [A] [C] -- 7
                // Price [C] [B0]
                result = p - c - ((p - c) * dp)
            }

            // 3 Var A
            12 -> { // Price [C] [B1] = Price [B1] [C] -- 9
                // Price [A] [B0] [C]
                result = p + (p * t) - ((p + (p * t)) * dp) - c
            }

            13 -> {
                // Price [A] [B1] [C]
                result = p + (p * t) - (p * dp) - c
            }

            14 -> {
                // Price [A] [C] [B0]
                result = p + (p * t) - c - ((p + (p * t) - c) * dp)
            }

            // 3 Var B
            15 -> { // Price [A] [C] [B1] = Price [A] [B1] [C] == 13
                // Price [B] [A0] [C]
                result = p - (p * dp) + ((p - (p * dp)) * t) - c
            }

            16 -> { // Price [B] [A1] [C] = Price [A] [B1] [C] -- 13
                // Price [B] [C] [A0]
                result = p - (p * dp) - c + ((p - (p * dp) - c) * t)
            }

            // 3 Var C
            17 -> { // Price [B] [C] [A1] = Price [A] [B1] [C] -- 13
                // Price [C] [A0] [B0]
                result = p - c + ((p - c) * t) - ((p - c + ((p - c) * t)) * dp)
            }

            18 -> {
                // Price [C] [A0] [B1]
                result = p - c + ((p - c) * t) - (p * dp)
            }

            19 -> {
                // Price [C] [A1] [B0]
                result = p - c + (p * t) - ((p - c + (p * t)) * dp)
            }

            20 -> { // Price [C] [A1] [B1] = Price [A] [B1] [C] -- 13
                result = p - c - ((p - c) * dp) + ((p - c - ((p - c) * dp)) * t)
            }

            21 -> {
                // Price [C] [B0] [A1]
                result = p - c - ((p - c) * dp) + (p * t)
            }

            22 -> {
                // Price [C] [B1] [A0]
                result = p - c - (p * dp) + ((p - c - (p * dp)) * t)
            }// Price [C] [B1] [A1] = Price [A] [B1] [C] -- 13

            else -> {
                // Do Nothing?
                result = 0.00
            }
        }
        return result
    }
}