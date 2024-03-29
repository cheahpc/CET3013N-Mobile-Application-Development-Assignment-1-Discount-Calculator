package com.example.a1discountcalculatorapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import com.example.a1discountcalculatorapplication.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var prevFocusView: EditText
    private lateinit var dvm: DiscountView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Refresh UI
        uiInit()
    }

    override fun onPause() {
        super.onPause()

        // Set Formula
        dvm.formula = b.tvFormula.text.toString()

        dvm.priceError = b.ettPrice.error != null
        dvm.taxError = b.ettTaxRate.error != null
        dvm.discountError = b.ettDiscountPercent.error != null
        dvm.couponError = b.ettDiscountFixed.error != null

        // Check Toggle
        dvm.taxToggle = b.switchT.isChecked
        dvm.discountToggle = b.switchDp.isChecked
        dvm.couponToggle = b.switchDf.isChecked

        // Check Switch
        dvm.taxSwitch = b.switchTaxRate.isChecked
        dvm.taxSwitch_enable = b.switchT.isEnabled
        dvm.discountSwitch = b.switchPercent.isChecked
        dvm.discountSwitch_enable = b.switchPercent.isEnabled

        // Check Sequence and set formula arrangement
        dvm.sequence = ""
        val cont = b.resultContainer
        for (x in 3..cont.childCount) {
            when (cont.getChildAt(x)) {
                b.row2 -> {
                    dvm.sequence = dvm.sequence + "A"
                }

                b.row3 -> {
                    dvm.sequence = dvm.sequence + "B"
                }

                b.row4 -> {
                    dvm.sequence = dvm.sequence + "C"
                }
            }
        }

        // Set Focus
        dvm.priceFocus = b.ettPrice.isFocused
        dvm.taxFocus = b.ettTaxRate.isFocused
        dvm.dpFocus = b.ettDiscountPercent.isFocused
        dvm.couponFocus = b.ettDiscountFixed.isFocused
    }

    override fun onResume() {
        super.onResume()

        val cont = b.resultContainer as ViewGroup

        // Set Formula string
        if (dvm.formula.isNotBlank()) {
            b.tvFormula.text = dvm.formula
        }

        // Set toggle
        b.switchT.isChecked = dvm.taxToggle
        b.switchDp.isChecked = dvm.discountToggle
        b.switchDf.isChecked = dvm.couponToggle

        // Set switch
        b.switchTaxRate.isChecked = dvm.taxSwitch
        b.switchTaxRate.isEnabled = dvm.taxSwitch_enable
        b.switchPercent.isChecked = dvm.discountSwitch
        b.switchPercent.isEnabled = dvm.discountSwitch_enable

        // Set sequence and add corresponding row
        if (dvm.sequence.isNotEmpty()) {
            cont.removeView(b.row2)
            cont.removeView(b.row3)
            cont.removeView(b.row4)
            for (x in 0..<dvm.sequence.length) {
                when (dvm.sequence[x]) {
                    'A' -> {
                        cont.addView(b.row2 as View)
                    }

                    'B' -> {
                        cont.addView(b.row3 as View)
                    }

                    'C' -> {
                        cont.addView(b.row4 as View)
                    }

                }
            }
        }

        // Set color
        if (dvm.priceError) {
            // Set Red
            b.row1.setBackgroundColor(getColor(R.color.faded_red))
            b.tvPrice.setTextColor(getColor(R.color.dark_red))
            b.ettPrice.setTextColor(getColor(R.color.dark_red))
        } else if (b.ettPrice.text.isNotEmpty() and (!b.ettPrice.isFocused)) {
            // Set Green
            b.row1.setBackgroundColor(getColor(R.color.light_green))
            b.tvPrice.setTextColor(getColor(R.color.dark_green))
            b.ettPrice.setTextColor(getColor(R.color.dark_green))
        }
        if (dvm.taxError) {
            // Set Red
            b.row2.setBackgroundColor(getColor(R.color.faded_red))
            b.switchTaxRate.setTextColor(getColor(R.color.dark_red))
            b.ettTaxRate.setTextColor(getColor(R.color.dark_red))
        } else if (b.ettTaxRate.text.isNotEmpty() and (!b.ettTaxRate.isFocused)) {
            // Set Green
            b.row2.setBackgroundColor(getColor(R.color.light_green))
            b.switchTaxRate.setTextColor(getColor(R.color.dark_green))
            b.ettTaxRate.setTextColor(getColor(R.color.dark_green))
        }
        if (dvm.discountError) {
            // Set Red
            b.row3.setBackgroundColor(getColor(R.color.faded_red))
            b.switchPercent.setTextColor(getColor(R.color.dark_red))
            b.ettDiscountPercent.setTextColor(getColor(R.color.dark_red))
        } else if (b.ettDiscountPercent.text.isNotEmpty() and (!b.ettDiscountPercent.isFocused)) {
            // Set Green
            b.row3.setBackgroundColor(getColor(R.color.light_green))
            b.switchPercent.setTextColor(getColor(R.color.dark_green))
            b.ettDiscountPercent.setTextColor(getColor(R.color.dark_green))
        }
        if (dvm.couponError) {
            // Set Red
            b.row4.setBackgroundColor(getColor(R.color.faded_red))
            b.tvFixed.setTextColor(getColor(R.color.dark_red))
            b.ettDiscountFixed.setTextColor(getColor(R.color.dark_red))
        } else if (b.ettDiscountFixed.text.isNotEmpty() and (!b.ettDiscountFixed.isFocused)) {
            // Set Green
            b.row4.setBackgroundColor(getColor(R.color.light_green))
            b.tvFixed.setTextColor(getColor(R.color.dark_green))
            b.ettDiscountFixed.setTextColor(getColor(R.color.dark_green))
        }

        // Set focus
        if(dvm.priceFocus) b.ettPrice.requestFocus()
        if(dvm.taxFocus) b.ettTaxRate.requestFocus()
        if(dvm.dpFocus) b.ettDiscountPercent.requestFocus()
        if(dvm.couponFocus) b.ettDiscountFixed.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        // Set flip option to enabled in landscape mode and false in portrait mode
        menu.findItem(R.id.menu_flip).isEnabled = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> true
            Configuration.ORIENTATION_PORTRAIT -> false
            else -> false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Reset every thing
            R.id.menu_reset -> {
                // Set background color
                b.row1.setBackgroundColor(getColor(R.color.transparent))
                b.row2.setBackgroundColor(getColor(R.color.transparent))
                b.row3.setBackgroundColor(getColor(R.color.transparent))
                b.row4.setBackgroundColor(getColor(R.color.transparent))

                // Set text color
                b.tvPrice.setTextColor(getColor(R.color.dark_blue))
                b.switchTaxRate.setTextColor(getColor(R.color.dark_blue))
                b.switchPercent.setTextColor(getColor(R.color.dark_blue))
                b.tvFixed.setTextColor(getColor(R.color.dark_blue))
                
                // Set focus
                switchRowFocus()

                // Reset all text
                b.ettPrice.text = null
                b.ettTaxRate.text = null
                b.ettDiscountPercent.text = null
                b.ettDiscountFixed.text = null

                // Reset edit text error
                b.ettPrice.error = null
                b.ettTaxRate.error = null
                b.ettDiscountPercent.error = null
                b.ettDiscountFixed.error = null
            }

            // Flip the screen
            R.id.menu_flip -> {
                // Get the current row order
                prevFocusView = currentFocus as EditText
                val container = b.container as ViewGroup
                val v1 = container.getChildAt(0)
                val v2 = container.getChildAt(1)
                val v3 = container.getChildAt(2)

                // Set animation and duration
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

            // Show the about fragment
            R.id.menu_about -> {
                AboutView().show(supportFragmentManager, "about_fragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun calculate() {
        val rCont = b.resultContainer
        var result = 0.0
        var fString = ""
        // Get values into variables
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
        val x = Discount(vPrice, vTax, vDP, vDF)

        // Check the frist row and determine the subsequent row. Base on that, use the corresponding formula to calculate the result
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
                        result = 0.0
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
                        result = 0.0
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
                        result = 0.0
                    }
                }
            }
        }

        // Update Result and refresh UI 
        result = BigDecimal(result).setScale(2, RoundingMode.HALF_UP).toDouble()
        b.tvResult.text = result.toString()
        b.tvFormula.text = fString
    }

    private fun focusRow(previous: Boolean) {
        // Customized function to shift focus to next row or previous row
        // Used for up and down button in onClickListener
        // Focus next row if the previous argument is false
        // Focus previous row if the previous argument is true
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
        // Switches focus between rows
        // A customized funciton to process the row swtich or focus swtich 
        // so the correct color, error, and value is set correctly
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

        // Process Previous Row, set color, text, and error etc
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

        // Process Current Row, set color, error, etc
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
        // Highlight the current row
        prevLabel.setTextColor(getColor(R.color.white))
        prevFocusView.setTextColor(getColor(R.color.white))
        prevFocusView.error = null
        prevRow.setBackgroundColor(getColor(R.color.dark_blue))
    }

    private fun focus(): EditText {
        // A custom function that returns the current focus edit text view
        // Check if there is something being focused, if nothing is being focused, 
        // force focus to ettPrice and return the ettPrice EditText
        if (currentFocus != null) {
            return currentFocus as EditText
        }
        b.ettPrice.requestFocus()
        return b.ettPrice
    }

    private fun uiInit() {
        // Custom fucntion to group all codes requried to setup the main activity.
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        // Setup UI
        setSupportActionBar(b.toolbar)
        // Set view model
        dvm = ViewModelProvider(this)[DiscountView::class.java]
        
        // Disable soft keyboard for all edit text field
        b.ettPrice.showSoftInputOnFocus = false
        b.ettTaxRate.showSoftInputOnFocus = false
        b.ettDiscountPercent.showSoftInputOnFocus = false
        b.ettDiscountFixed.showSoftInputOnFocus = false

        // set initial focus
        b.ettPrice.requestFocus()
        prevFocusView = focus()

        // Set animation and animation duration
        val enterAnimation = AnimationUtils.makeInAnimation(this, true)
        val exitAnimation = AnimationUtils.makeOutAnimation(this, true)
        enterAnimation.duration = 250
        exitAnimation.duration = 250

        // On click
        b.btnDown.setOnClickListener { focusRow(false) }
        b.btnUp.setOnClickListener { focusRow(true) }
        b.btnBack.setOnClickListener {
            // Backspace key, remove one character from the edit text field 
            // base on the current cursor position.
            val length = focus().length()
            if (length > 0) {
                val start = focus().selectionStart
                val end = focus().selectionEnd
                if (start > 0) {
                    if (end - start > 0) {
                        // Delete selection
                        focus().text.delete(start, end)
                    } else {
                        // Delete only one character
                        focus().text.delete(start - 1, start)
                    }
                }
            }
        }
        b.btnAllClear.setOnClickListener {
            // Clear the current focused edit text view
            focus().text = null
            focus().error = null
        }
        b.tvResult.setOnClickListener {
            // set clipboard service
            val clipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // copy the result content to clipboard
            val clipData = ClipData.newPlainText("text", b.tvResult.text)
            clipboardManager.setPrimaryClip(clipData)
            // Display coppy successful message
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
            // Display corresponding message for tax rate calculation method
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
            // Display corresponding message for percent off calculation method
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
                // Process row format when switch focus
                switchRowFocus()
            }
            // Re-calculate and refresh the UI
            calculate()
        }
        b.ettTaxRate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Process row format when switch focus
                switchRowFocus()
            }
            // Re-calculate and refresh the UI
            calculate()
        }
        b.ettDiscountPercent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Process row format when switch focus
                switchRowFocus()
            }
            // Re-calculate and refresh the UI
            calculate()
        }
        b.ettDiscountFixed.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Process row format when switch focus
                switchRowFocus()
            }
            // Re-calculate and refresh the UI
            calculate()
        }
    }

    @Suppress("unused")
    fun numKeyAction(view: View) {
        // Function of OnClick action for all the number buttons
        // actually applied in xml layout file through OnClickAction attribute
        // Set regular expression for checking decimal
        val defaultDecimalFormat = Regex("^[0-9]*.?[0-9]{0,2}$")
        if (view is Button) {
            val start = focus().selectionStart
            val end = focus().selectionEnd
            // Delete the selected text for replacing with new digit
            if (end - start > 0) {
                focus().text.delete(start, end)
            }
            // Check if the text of the current focus is empty.
            if (focus().text.isEmpty()) {
                // If it is empty and the button pressed is "." then add 0 in front
                if (view.text.equals(".")) {
                    focus().setText("0.")
                    focus().setSelection(focus().length())
                    return
                }
            }
            // Get the existing text in the edit text view and add the intended character to existing text 
            // to the temp string for checking if the text complies with the regex format 
            var temp: String = focus().text.toString()
            temp = temp.substring(0, start) + view.text + temp.substring(start)

            // If the temporary string match with the defined regex format, allow the charracter input
            if (temp.matches(defaultDecimalFormat)) {
                focus().text.insert(start, view.text)
            }
        }
    }
}