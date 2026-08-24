package com.kyf.knowyourfood.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MicronutrientProfile(
    val vit_a_mcg: Double? = null,
    val vit_c_mg: Double? = null,
    val vit_d_mcg: Double? = null,
    val vit_e_mg: Double? = null,
    val vit_k_mcg: Double? = null,
    val vit_b6_mg: Double? = null,
    val thiamin_mg: Double? = null,
    val riboflavin_mg: Double? = null,
    val niacin_mg: Double? = null,
    val folate_mcg: Double? = null,
    val vit_b12_mcg: Double? = null,
    val calcium_mg: Double? = null,
    val iron_mg: Double? = null,
    val potassium_mg: Double? = null,
    val magnesium_mg: Double? = null,
    val phosphorus_mg: Double? = null,
    val zinc_mg: Double? = null,
    val copper_mg: Double? = null,
    val manganese_mg: Double? = null,
    val selenium_mcg: Double? = null,
    val sodium_mg: Double? = null,
    val choline_mg: Double? = null,
    val omega3_ala_g: Double? = null,
    val epa_dha_omega3_g: Double? = null,
    val lycopene_mcg: Double? = null,
    val lutein_mcg: Double? = null,
    val alpha_gal_present: Boolean? = null,
    val allergenic_pollen_cross: List<String> = emptyList(),
    val notes: String? = null
)

data class RawFoodItem(
    val fdcId: Long,
    val name: String,
    val category: String,
    val servingG: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val iron: Double,
    val vitC: Double,
    val energyKcal: Double,
    val micronutrients: MicronutrientProfile,
    val source: String
) {
    /**
     * Scales nutrients to a specified gram weight from the base 100g.
     */
    fun scaleTo(targetGrams: Double): ScaledNutrition {
        val factor = targetGrams / 100.0
        return ScaledNutrition(
            grams = targetGrams,
            energyKcal = energyKcal * factor,
            protein = protein * factor,
            carbs = carbs * factor,
            fat = fat * factor,
            fiber = fiber * factor,
            iron = iron * factor,
            vitC = vitC * factor,
            potassium = (micronutrients.potassium_mg ?: 0.0) * factor,
            calcium = (micronutrients.calcium_mg ?: 0.0) * factor,
            magnesium = (micronutrients.magnesium_mg ?: 0.0) * factor,
            sodium = (micronutrients.sodium_mg ?: 0.0) * factor
        )
    }
}

data class ScaledNutrition(
    val grams: Double,
    val energyKcal: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val iron: Double,
    val vitC: Double,
    val potassium: Double,
    val calcium: Double,
    val magnesium: Double,
    val sodium: Double
)
