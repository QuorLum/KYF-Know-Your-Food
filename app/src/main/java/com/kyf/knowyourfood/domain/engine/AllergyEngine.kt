package com.kyf.knowyourfood.domain.engine

import com.kyf.knowyourfood.data.model.*
import kotlinx.serialization.json.Json

object AllergyEngine {

    private val json = Json { ignoreUnknownKeys = true }

    // Dictionary mapping allergens to search keywords and synonyms
    private val allergenKeywords: Map<String, List<String>> = mapOf(
        "PEANUT" to listOf("peanut", "peanuts", "groundnut", "groundnuts", "arachis", "monkey nut"),
        "TREE_NUTS" to listOf("tree nut", "tree nuts", "almond", "walnut", "cashew", "pistachio", "pecan", "hazelnut", "brazil nut", "macadamia", "pine nut", "chestnut"),
        "ALMOND" to listOf("almond", "almonds"),
        "HAZELNUT" to listOf("hazelnut", "hazelnuts", "filbert"),
        "MILK" to listOf("milk", "dairy", "casein", "caseinate", "whey", "butter", "cheese", "cream", "lactose", "curd", "ghee", "paneer", "yogurt", "yoghurt", "milk solids", "milk fat"),
        "EGG" to listOf("egg", "eggs", "egg white", "egg yolk", "albumin", "ovalbumin", "ovomucoid", "mayonnaise", "lysozyme", "meringue"),
        "WHEAT" to listOf("wheat", "flour", "durum", "semolina", "spelt", "farina", "emmer", "einkorn", "kamut", "bulgur", "couscous", "maida"),
        "GLUTEN" to listOf("gluten", "wheat", "barley", "rye", "malt", "malted", "triticale", "seitan"),
        "BARLEY" to listOf("barley", "barley malt", "malt extract"),
        "OATS" to listOf("oat", "oats", "oatmeal", "oat flour", "rolled oats"),
        "SOYBEANS" to listOf("soy", "soya", "soybean", "soybeans", "edamame", "tofu", "tempeh", "miso", "soy lecithin", "soya lecithin", "hydrolysed soy protein"),
        "FISH" to listOf("fish", "cod", "salmon", "tuna", "haddock", "pollock", "anchovy", "sardine", "tilapia", "trout", "halibut", "parvalbumin", "fish gelatin", "fish sauce"),
        "CRUSTACEANS" to listOf("crustacean", "crustaceans", "shrimp", "prawn", "prawns", "crab", "lobster", "crayfish", "krill", "tropomyosin"),
        "MOLLUSCS" to listOf("mollusc", "molluscs", "mollusk", "mollusks", "clam", "clams", "oyster", "oysters", "mussel", "mussels", "scallop", "scallops", "squid", "calamari", "octopus", "snail", "escargot"),
        "SESAME" to listOf("sesame", "sesame seed", "sesame seeds", "tahini", "sesamum", "gingelly", "til"),
        "CELERY" to listOf("celery", "celeriac", "celery seed", "celery salt", "celery root"),
        "MUSTARD" to listOf("mustard", "mustard seed", "mustard powder", "mustard flour", "dijon"),
        "SULPHITES" to listOf("sulphite", "sulphites", "sulfite", "sulfites", "sulfur dioxide", "sulphur dioxide", "sodium metabisulphite", "potassium metabisulphite", "e220", "e221", "e222", "e223", "e224", "e226", "e227", "e228"),
        "LUPIN" to listOf("lupin", "lupine", "lupini", "lupin flour")
    )

    // Cross-Reactivity Profiles from Source 1
    private val pollenCrossReactivityMap: Map<String, List<String>> = mapOf(
        "BIRCH" to listOf("apple", "pear", "peach", "plum", "apricot", "cherry", "kiwi", "carrot", "celery", "parsley", "almond", "hazelnut", "soy", "soya", "peanut", "coriander"),
        "GRASS" to listOf("tomato", "melon", "watermelon", "orange", "peach", "potato", "white potato"),
        "RAGWEED" to listOf("banana", "melon", "cantaloupe", "honeydew", "watermelon", "cucumber", "zucchini", "sunflower seed", "sunflower"),
        "MUGWORT" to listOf("celery", "carrot", "fennel", "coriander", "parsley", "sunflower", "bell pepper", "capsicum", "cabbage", "broccoli", "cauliflower"),
        "LATEX" to listOf("banana", "avocado", "kiwi", "chestnut", "papaya", "fig")
    )

    // Non-IgE / Special Conditions Triggers
    private val nonIgETriggers: Map<String, List<String>> = mapOf(
        "CELIAC" to listOf("wheat", "barley", "rye", "gluten", "malt", "spelt", "kamut", "triticale", "semolina", "durum", "bulgur", "maida"),
        "FPIES" to listOf("milk", "dairy", "soy", "soya", "rice", "oat", "oats", "fish", "shellfish"),
        "ALPHA_GAL" to listOf("beef", "pork", "lamb", "mutton", "veal", "venison", "bacon", "ham", "gelatin", "mammal", "meat extract"),
        "LACTOSE_INTOLERANCE" to listOf("milk", "lactose", "whey", "cheese", "cream", "butter", "milk powder", "condensed milk", "ice cream"),
        "HISTAMINE_INTOLERANCE" to listOf("aged cheese", "cured meat", "salami", "wine", "fermented", "sauerkraut", "anchovy", "canned fish"),
        "SULFITE_SENSITIVITY" to listOf("sulphite", "sulphites", "sulfite", "sulfites", "sulfur dioxide", "sulphur dioxide", "metabisulphite", "dried fruit", "wine")
    )

    /**
     * Evaluates a product against an active user profile.
     */
    fun evaluateProductSafety(
        product: ProductEntity,
        profile: ProfileEntity
    ): SafetyAssessment {
        val userAllergyProfile: AllergyProfile = try {
            json.decodeFromString(profile.allergiesJson)
        } catch (e: Exception) {
            AllergyProfile()
        }

        val productAllergenTags: AllergenTags = try {
            json.decodeFromString(product.allergensJson)
        } catch (e: Exception) {
            AllergenTags()
        }

        val directMatches = mutableListOf<AllergenRiskMatch>()
        val traceMatches = mutableListOf<AllergenRiskMatch>()
        val pollenMatches = mutableListOf<AllergenRiskMatch>()
        val nonIgEMatches = mutableListOf<AllergenRiskMatch>()
        val ageAlerts = mutableListOf<AgeAlert>()

        val ingredientsLower = product.ingredientsText.lowercase()

        // 1. Direct Regulated Major Allergens Check
        for (userAllergen in userAllergyProfile.allergens) {
            val userAllergenUpper = userAllergen.uppercase()
            val synonyms = allergenKeywords[userAllergenUpper] ?: listOf(userAllergen.lowercase())

            // Check product explicit "contains" list
            val matchesExplicitContains = productAllergenTags.contains.any { tag ->
                tag.equals(userAllergenUpper, ignoreCase = true) || synonyms.any { s -> tag.contains(s, ignoreCase = true) }
            }

            // Check ingredient text matches
            val matchedKeyword = synonyms.find { kw ->
                containsWholeWord(ingredientsLower, kw)
            }

            if (matchesExplicitContains || matchedKeyword != null) {
                directMatches.add(
                    AllergenRiskMatch(
                        triggerName = MajorAllergen.fromId(userAllergenUpper)?.displayName ?: userAllergen,
                        allergenCategory = "Direct Major Allergen",
                        matchedTerm = matchedKeyword ?: userAllergen,
                        isTrace = false,
                        description = "Direct ingredient conflict with your profile allergy ($userAllergen)."
                    )
                )
            }

            // Check "May Contain" traces
            val matchesExplicitTrace = productAllergenTags.may_contain.any { tag ->
                tag.equals(userAllergenUpper, ignoreCase = true) || synonyms.any { s -> tag.contains(s, ignoreCase = true) }
            }
            val hasMayContainInText = containsTraceWarning(ingredientsLower, synonyms)

            if (matchesExplicitTrace || hasMayContainInText) {
                // If not already flagged as direct
                if (directMatches.none { it.triggerName.contains(userAllergen, ignoreCase = true) }) {
                    traceMatches.add(
                        AllergenRiskMatch(
                            triggerName = MajorAllergen.fromId(userAllergenUpper)?.displayName ?: userAllergen,
                            allergenCategory = "Precautionary Trace / Facility Risk",
                            matchedTerm = "May contain traces",
                            isTrace = true,
                            description = "Manufactured in a facility handling $userAllergen. Precautionary warning."
                        )
                    )
                }
            }
        }

        // 2. Non-IgE Conditions & Sensitivities (Celiac, FPIES, Alpha-gal, Lactose, etc.)
        for (conditionId in userAllergyProfile.conditions) {
            val conditionUpper = conditionId.uppercase()
            val triggers = nonIgETriggers[conditionUpper] ?: emptyList()
            val matchedTrigger = triggers.find { tr -> containsWholeWord(ingredientsLower, tr) }

            if (matchedTrigger != null) {
                val condition = NonIgECondition.fromId(conditionUpper)
                nonIgEMatches.add(
                    AllergenRiskMatch(
                        triggerName = condition?.displayName ?: conditionUpper,
                        allergenCategory = "Non-IgE / Special Condition",
                        matchedTerm = matchedTrigger,
                        isTrace = false,
                        description = "${condition?.displayName ?: conditionUpper} trigger detected: '$matchedTrigger'. ${condition?.description ?: ""}"
                    )
                )
            }
        }

        // 3. Pollen-Food Cross-Reactivity Syndromes (Birch, Grass, Ragweed, Mugwort, Latex)
        for (pollenId in userAllergyProfile.pollenSensitivities) {
            val pollenUpper = pollenId.uppercase()
            val crossFoods = pollenCrossReactivityMap[pollenUpper] ?: emptyList()
            val matchedCrossFood = crossFoods.find { food -> containsWholeWord(ingredientsLower, food) }

            if (matchedCrossFood != null) {
                val syndrome = PollenSyndrome.fromId(pollenUpper)
                pollenMatches.add(
                    AllergenRiskMatch(
                        triggerName = syndrome?.displayName ?: pollenUpper,
                        allergenCategory = "Pollen Cross-Reactivity (OAS)",
                        matchedTerm = matchedCrossFood,
                        isTrace = false,
                        description = "Oral Allergy Syndrome trigger ($matchedCrossFood) cross-reacts with ${syndrome?.triggerPollen ?: pollenUpper}."
                    )
                )
            }
        }

        // 4. Age & Nutrient Guardrails (e.g., Child Profile Sugar Cap)
        if (profile.age < 12 && product.sugars100g > 22.5) {
            ageAlerts.add(
                AgeAlert(
                    title = "High Added Sugar for Child Profile",
                    reason = "Scanned item contains ${product.sugars100g}g sugar/100g (exceeds child guideline threshold of 22.5g/100g).",
                    thresholdValue = "> 22.5g / 100g"
                )
            )
        }
        if (profile.age < 12 && product.salt100g > 1.5) {
            ageAlerts.add(
                AgeAlert(
                    title = "High Sodium for Child Profile",
                    reason = "Scanned item contains ${product.salt100g}g salt/100g (exceeds pediatric recommended limit).",
                    thresholdValue = "> 1.5g / 100g"
                )
            )
        }

        // 5. Determine Overall Safety Status
        val status: SafetyStatus = when {
            directMatches.isNotEmpty() || nonIgEMatches.isNotEmpty() || ageAlerts.isNotEmpty() -> SafetyStatus.UNSAFE
            userAllergyProfile.strictTraces && traceMatches.isNotEmpty() -> SafetyStatus.UNSAFE
            traceMatches.isNotEmpty() || pollenMatches.isNotEmpty() -> SafetyStatus.CAUTION
            else -> SafetyStatus.SAFE
        }

        val scoreText = when (status) {
            SafetyStatus.SAFE -> "Product is safe for ${profile.name}'s allergen and health profile."
            SafetyStatus.CAUTION -> "Caution advised: Detected trace allergen warnings or pollen cross-reactivity triggers."
            SafetyStatus.UNSAFE -> "Not Recommended: Direct allergen conflict or pediatric health constraint detected."
        }

        val trafficLights = SafetyRecommendationEngine.evaluateTrafficLights(product)

        return SafetyAssessment(
            status = status,
            overallScoreText = scoreText,
            directAllergenMatches = directMatches,
            traceAllergenMatches = traceMatches,
            pollenCrossMatches = pollenMatches,
            nonIgEMatches = nonIgEMatches,
            ageAlerts = ageAlerts,
            trafficLights = trafficLights
        )
    }

    private fun containsWholeWord(text: String, word: String): Boolean {
        val regex = Regex("\\b${Regex.escape(word.lowercase())}\\b", RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(text)
    }

    private fun containsTraceWarning(text: String, keywords: List<String>): Boolean {
        val traceIndicators = listOf("may contain", "trace", "traces", "factory handles", "facility that processes")
        val hasTracePhrase = traceIndicators.any { text.contains(it) }
        return hasTracePhrase && keywords.any { containsWholeWord(text, it) }
    }
}
