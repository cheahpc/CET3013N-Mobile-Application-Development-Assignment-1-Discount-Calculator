package com.example.a1discountcalculatorapplication

import androidx.lifecycle.ViewModel

class DiscountView : ViewModel() {

    var formula = ""

    // Focus
    var priceFocus = true
    var taxFocus = false
    var dpFocus = false
    var couponFocus = false

    // Toggle
    var taxToggle = true
    var discountToggle = true
    var couponToggle = true

    // Switch
    var taxSwitch = true
    var discountSwitch = false
    var discountSwitch_enable = false
    var taxSwitch_enable = true

    // Formula Sequence
    var sequence = "ABC"

    var priceError = false
    var taxError = false
    var discountError = false
    var couponError = false

}