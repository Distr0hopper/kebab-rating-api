package com.fladenchef.rating.model.enums

enum class PriceRange(val symbol: String, val description: String) {
    CHEAP("€", "Under €5"),
    MEDIUM("€€", "€5 - €8"),
    EXPENSIVE("€€€", "Over €8");

    companion object {
        fun fromPrice(price: Double): PriceRange {
            return when {
                price < 5.0 -> CHEAP
                price in 5.0..8.0 -> MEDIUM
                else -> EXPENSIVE
            }
        }
    }
}