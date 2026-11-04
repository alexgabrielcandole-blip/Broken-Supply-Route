package com.example.data

/**
 * Tactical briefing mode preference for The Broken Supply Route.
 *
 * GUIDED: Step-by-step tactical guidance explaining each strategic decision, enemy intent,
 *         status timing, and formation counter in detail.
 * COMPACT: Streamlined briefing grouping explanations into concise summaries.
 *
 * Both modes receive identical entitlements, costs, combat math, and save states.
 */
enum class DispatchMode(val serializedName: String) {
    GUIDED("GUIDED"),
    COMPACT("COMPACT");

    companion object {
        fun fromSerialized(name: String?): DispatchMode? {
            return entries.firstOrNull { it.serializedName.equals(name, ignoreCase = true) }
        }
    }
}
