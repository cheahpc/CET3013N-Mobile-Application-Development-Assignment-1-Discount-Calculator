package com.example.a1discountcalculatorapplication

class Discount(
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
                result = 0.0
            }
        }
        return result
    }
}