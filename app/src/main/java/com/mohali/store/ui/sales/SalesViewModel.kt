package com.mohali.store.ui.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.local.SaleDao
import com.mohali.store.data.models.Sale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val saleDao: SaleDao
) : ViewModel() {

    val sales: StateFlow<List<Sale>> = saleDao.getAllSales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalToday = MutableStateFlow(0.0)
    val totalToday: StateFlow<Double> = _totalToday

    private val _totalMonth = MutableStateFlow(0.0)
    val totalMonth: StateFlow<Double> = _totalMonth

    init {
        loadTotals()
    }

    private fun loadTotals() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val end = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
            val startDay = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val startMonth = cal.timeInMillis
            _totalToday.value = saleDao.getTotalSalesAmount(startDay, end) ?: 0.0
            _totalMonth.value = saleDao.getTotalSalesAmount(startMonth, end) ?: 0.0
        }
    }
}
