package com.toybox.app

import java.text.SimpleDateFormat
import java.util.*

object InsightEngine {

    // ── Collection headline ───────────────────────────────────────────────────

    fun collectionHeadline(count: Int): String = when {
        count == 0  -> "Your toy box is empty 😢  Let's fix that!"
        count == 1  -> "Your first toy! The adventure begins 🚀"
        count < 5   -> "Growing fast! You have $count toys! 🌱"
        count < 10  -> "You have $count awesome toys! 🎉"
        count < 20  -> "Super Collector! $count toys is impressive! 🏆"
        else        -> "LEGENDARY! $count toys — you rule! 👑"
    }

    // ── Spending insight ──────────────────────────────────────────────────────

    fun spentInsight(spent: Double): String = when {
        spent <= 0    -> "Nothing spent yet!"
        spent < 500   -> "Smart shopper! ₹${spent.toInt()} well spent 💸"
        spent < 2000  -> "Invested ₹${spent.toInt()} in awesome toys 🎯"
        else          -> "Epic ₹${spent.toInt()} collection! 🏆"
    }

    // ── Favourite category ────────────────────────────────────────────────────

    fun favoriteCategory(toys: List<Toy>): String {
        if (toys.isEmpty()) return "No favourites yet!"
        val top = toys.groupBy { it.category }.maxByOrNull { it.value.size }
            ?: return "No favourites yet!"
        val emoji = EMOJI_MAP[top.key] ?: "🎁"
        return "You love ${top.key}! $emoji (${top.value.size} toys)"
    }

    // ── Monthly progress ──────────────────────────────────────────────────────

    fun monthlyInsight(toys: List<Toy>): String {
        val count = monthlyCount(toys)
        return when {
            count == 0 -> "No new toys this month yet 🤔"
            count == 1 -> "1 new toy this month! Keep going ⚡"
            else       -> "$count new toys this month! 🔥"
        }
    }

    fun monthlyCount(toys: List<Toy>): Int {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        return toys.count { toy ->
            if (toy.purchaseDate.isBlank()) return@count false
            try {
                val c = Calendar.getInstance().also { it.time = fmt.parse(toy.purchaseDate)!! }
                c.get(Calendar.MONTH) == m && c.get(Calendar.YEAR) == y
            } catch (_: Exception) { false }
        }
    }

    // ── Category breakdown list ───────────────────────────────────────────────

    fun categoryBreakdown(toys: List<Toy>): List<Pair<String, Int>> =
        toys.groupBy { it.category }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }

    // ── Local emoji map (mirrors Theme.kt) ───────────────────────────────────

    val EMOJI_MAP = mapOf(
        "LEGO" to "🧱", "Action figure" to "🦸", "Stuffed animal" to "🧸",
        "Vehicle" to "🚗", "Board game" to "🎲", "Puzzle" to "🧩",
        "Remote control" to "🎮", "Outdoor toy" to "⛺", "Arts & crafts" to "🎨",
        "Building blocks" to "🏗", "Doll" to "🪆", "Musical toy" to "🎵",
        "Educational" to "📚", "Sports" to "⚽", "Card game" to "🃏",
        "Science kit" to "🔬", "Other" to "🎁"
    )
}
