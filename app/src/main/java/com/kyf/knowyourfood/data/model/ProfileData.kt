package com.kyf.knowyourfood.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllergyProfile(
    val allergens: List<String> = emptyList(), // e.g. "PEANUT", "TREE_NUTS", "MILK", "EGG", "WHEAT", "SOYBEANS", "FISH", "CRUSTACEANS", "SESAME", "CELERY", "MUSTARD", "SULPHITES", "LUPIN", "MOLLUSCS"
    @SerialName("pollen_sensitivities")
    val pollenSensitivities: List<String> = emptyList(), // "BIRCH", "GRASS", "RAGWEED", "MUGWORT", "LATEX"
    val conditions: List<String> = emptyList(), // "CELIAC", "FPIES", "ALPHA_GAL", "LACTOSE_INTOLERANCE", "HISTAMINE_INTOLERANCE", "SULFITE_SENSITIVITY", "EOSINOPHILIC_ESOPHAGITIS"
    @SerialName("strict_traces")
    val strictTraces: Boolean = true // Flag whether 'may contain' trace warnings should trigger high alerts
)

enum class MajorAllergen(val id: String, val displayName: String, val category: String) {
    // FDA 9 Base
    MILK("MILK", "Milk & Dairy", "FDA 9 / EU 14 / FSSAI 8"),
    EGGS("EGG", "Eggs", "FDA 9 / EU 14 / FSSAI 8"),
    FISH("FISH", "Fish", "FDA 9 / EU 14 / FSSAI 8"),
    CRUSTACEANS("CRUSTACEANS", "Crustaceans (Shrimp/Crab)", "FDA 9 / EU 14 / FSSAI 8"),
    TREE_NUTS("TREE_NUTS", "Tree Nuts (Almond/Walnut/Cashew)", "FDA 9 / EU 14 / FSSAI 8"),
    PEANUTS("PEANUT", "Peanuts", "FDA 9 / EU 14 / FSSAI 8"),
    WHEAT("WHEAT", "Wheat & Gluten Cereals", "FDA 9 / EU 14 / FSSAI 8"),
    SOYBEANS("SOYBEANS", "Soybeans & Soy", "FDA 9 / EU 14 / FSSAI 8"),
    SESAME("SESAME", "Sesame", "FDA 9 / EU 14"),

    // EU 14 & FSSAI 8 Extensions
    CELERY("CELERY", "Celery & Celeriac", "EU 14"),
    MUSTARD("MUSTARD", "Mustard & Seeds", "EU 14 / FSSAI"),
    SULPHITES("SULPHITES", "Sulphites / Sulfur Dioxide (>10mg/kg)", "EU 14 / FSSAI 8"),
    LUPIN("LUPIN", "Lupin", "EU 14"),
    MOLLUSCS("MOLLUSCS", "Molluscs (Clams/Oysters/Squid)", "EU 14");

    companion object {
        fun fromId(id: String): MajorAllergen? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}

enum class PollenSyndrome(val id: String, val displayName: String, val triggerPollen: String, val crossFoods: String) {
    BIRCH("BIRCH", "Birch Pollen Syndrome (OAS)", "Birch Pollen (Spring)", "Apple, Peach, Cherry, Carrot, Celery, Almond, Hazelnut, Soy, Peanut"),
    GRASS("GRASS", "Grass Pollen Syndrome", "Grass Pollen (Summer)", "Tomato, Melon, Orange, Peach, Potato"),
    RAGWEED("RAGWEED", "Ragweed Pollen Syndrome", "Ragweed Pollen (Fall)", "Banana, Melon, Watermelon, Cucumber, Zucchini, Sunflower Seed"),
    MUGWORT("MUGWORT", "Mugwort Pollen Syndrome", "Mugwort Pollen (Fall)", "Celery, Carrot, Fennel, Coriander, Bell Pepper, Cabbage"),
    LATEX("LATEX", "Latex-Fruit Syndrome", "Natural Rubber Latex", "Banana, Avocado, Kiwi, Chestnut");

    companion object {
        fun fromId(id: String): PollenSyndrome? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}

enum class NonIgECondition(val id: String, val displayName: String, val description: String) {
    CELIAC("CELIAC", "Celiac Disease", "Autoimmune response triggered by gluten proteins in wheat, barley, rye"),
    FPIES("FPIES", "FPIES (Food Protein Enterocolitis)", "Severe delayed gastrointestinal non-IgE reaction (cow's milk, soy, rice, oats, fish)"),
    ALPHA_GAL("ALPHA_GAL", "Alpha-gal Syndrome (Red Meat)", "Delayed allergic reaction to mammalian red meat (beef, pork, lamb), dairy & gelatin"),
    LACTOSE_INTOLERANCE("LACTOSE_INTOLERANCE", "Lactose Intolerance", "Enzymatic lactase deficiency causing digestive distress from dairy sugars"),
    HISTAMINE_INTOLERANCE("HISTAMINE_INTOLERANCE", "Histamine Intolerance", "Deficiency in DAO enzyme breaking down aged cheese, cured meats, wine"),
    SULFITE_SENSITIVITY("SULFITE_SENSITIVITY", "Sulfite Sensitivity", "Chemical sensitivity triggering bronchospasm and asthma symptoms");

    companion object {
        fun fromId(id: String): NonIgECondition? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}
