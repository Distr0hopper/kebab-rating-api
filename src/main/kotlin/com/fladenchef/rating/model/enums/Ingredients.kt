package com.fladenchef.rating.model.enums

enum class Ingredients(val displayNameDe: String, val displayNameEn: String) {
    SALAD("Salat", "Salad"),
    TOMATO("Tomate", "Tomato"),
    ONION("Zwiebel", "Onion"),
    CUCUMBER("Gurke", "Cucumber"),
    CHEESE("Käse", "Cheese"),
    CORN("Mais", "Corn"),
    CARROTS("Karotten", "Carrots"),;

    fun displayName(): String = displayNameDe

    fun displayNameEn(): String = displayNameEn
}