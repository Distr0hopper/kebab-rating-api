package com.fladenchef.rating.model.enums

enum class Sauces(val displayNameDe: String, val displayNameEn: String) {
    GARLIC("Knoblauchsauce", "Garlic Sauce"),
    HOT_SAUCE("Scharfe Sauce", "Hot Sauce"),
    YOGURT_HERB("Joghurt-Kräutersauce", "Yogurt Herb Sauce");

    fun displayName(): String = displayNameDe
    fun displayNameEn(): String = displayNameEn
}
