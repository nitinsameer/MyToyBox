package com.toybox.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ToyViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = ToyDatabase.getDatabase(app).toyDao()

    val toys: StateFlow<List<Toy>> = dao.getAllToys().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addToy(toy: Toy) = viewModelScope.launch { dao.insertToy(toy) }

    fun deleteToy(toy: Toy) = viewModelScope.launch { dao.deleteToy(toy) }

    fun updateToy(toy: Toy) = viewModelScope.launch { dao.updateToy(toy) }

    fun deleteAllToys() = viewModelScope.launch { dao.deleteAllToys() }
}