package com.kyf.knowyourfood.data.model

enum class SafetyStatus(val title: String, val badgeColor: Long) {
    SAFE("SAFE FOR YOU", 0xFF10B981),                    // Emerald Green
    CAUTION("USE CAUTION (TRACES / POLLEN)", 0xFFF59E0B), // Amber
    UNSAFE("NOT RECOMMENDED (ALLERGEN / AGE)", 0xFFEF4444) // Crimson Red
}

enum class RiskLevel {
    NONE,
    PRECAUTIONARY_TRACE,
    CROSS_REACTIVE_POLLEN,
    DIRECT_ALLERGEN,
    AGE_HEALTH_LIMIT
}

data class AllergenRiskMatch(
    val triggerName: String,
    val allergenCategory: String,
    val matchedTerm: String,
    val isTrace: Boolean = false,
    val description: String
)

data class AgeAlert(
    val title: String,
    val reason: String,
    val thresholdValue: String
)

data class SafetyAssessment(
    val status: SafetyStatus,
    val overallScoreText: String,
    val directAllergenMatches: List<AllergenRiskMatch> = emptyList(),
    val traceAllergenMatches: List<AllergenRiskMatch> = emptyList(),
    val pollenCrossMatches: List<AllergenRiskMatch> = emptyList(),
    val nonIgEMatches: List<AllergenRiskMatch> = emptyList(),
    val ageAlerts: List<AgeAlert> = emptyList(),
    val trafficLights: TrafficLights,
    val recommendedAlternatives: List<ProductItem> = emptyList()
) {
    val isSafe: Boolean get() = status == SafetyStatus.SAFE
    val hasDirectRisk: Boolean get() = directAllergenMatches.isNotEmpty() || nonIgEMatches.isNotEmpty() || ageAlerts.isNotEmpty()
}
