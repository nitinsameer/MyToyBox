package com.toybox.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toys")
data class Toy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    val purchaseDate: String,
    val purchaseKey: String,
    val photoUri: String = ""
)