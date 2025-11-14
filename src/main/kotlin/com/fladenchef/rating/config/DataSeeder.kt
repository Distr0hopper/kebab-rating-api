package com.fladenchef.rating.config

import com.fladenchef.rating.model.entity.BreadType
import com.fladenchef.rating.model.entity.MeatType
import com.fladenchef.rating.repository.BreadTypeRepository
import com.fladenchef.rating.repository.MeatTypeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataSeeder(
    private val breadTypeRepository: BreadTypeRepository,
    private val meatTypeRepository: MeatTypeRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        seedBreatTypes()
        seedMeatTypes()
    }

    private fun seedBreatTypes() {
        // Only insert when table is empty
        if(breadTypeRepository.count() == 0L) {
            val breadTypes = listOf(
                BreadType(name = "Sesam"),
                BreadType(name = "Dürüm"),
                BreadType(name = "Dreieckig"),
            )
            breadTypeRepository.saveAll(breadTypes)
            println("${breadTypes.size} bread types seeded.")
        }
    }

    private fun seedMeatTypes() {
        // Only insert when table is empty
        if(meatTypeRepository.count() == 0L) {
            val meatTypes = listOf(
                MeatType(name = "Hähnchen", isHalal = true),
                MeatType(name = "Rind", isHalal = false),
                MeatType(name = "Kalb", isHalal = true),
                MeatType(name = "Vegetarisch", isHalal = true),
            )
            meatTypeRepository.saveAll(meatTypes)
            println("${meatTypes.size} meat types seeded.")
        }
    }
}