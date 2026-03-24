package com.toybox.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToyDao {
    @Query("SELECT * FROM toys ORDER BY id DESC")
    fun getAllToys(): Flow<List<Toy>>

    @Query("SELECT * FROM toys WHERE id = :id")
    suspend fun getToyById(id: Int): Toy?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToy(toy: Toy)

    @Update
    suspend fun updateToy(toy: Toy)

    @Delete
    suspend fun deleteToy(toy: Toy)

    @Query("SELECT * FROM toys")
    suspend fun getAllToysOnce(): List<Toy>

    @Query("DELETE FROM toys")
    suspend fun deleteAllToys()
}