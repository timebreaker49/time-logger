package com.example.selfproductivityapp.entry

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.selfproductivityapp.database.ActivitiesDatabaseDao
import com.example.selfproductivityapp.database.ActivitiesDay
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EntryViewModel(private val selectedDate: String, val database: ActivitiesDatabaseDao
                     ): ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val newThing: ActivitiesDay

    private val brandNew: ActivitiesDay
        get() {
            return ActivitiesDay()
        }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _startTime = MutableLiveData<String>()
    val startTime: MutableLiveData<String>
        get() = _startTime

    private val _endTime = MutableLiveData<String>()
    val endTime: MutableLiveData<String>
        get() = _endTime

    init {
        _date.value = selectedDate
        _startTime.value = ""
        newThing = brandNew
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun timeToDateTime(enteredTime: String?): Long {
        val completeStart = "${date.value} $enteredTime"
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm")
        return LocalDateTime.parse(completeStart, formatter).toEpochSecond(ZoneOffset.UTC)
    }

    private suspend fun insert(entry: ActivitiesDay) {
        withContext(Dispatchers.IO) {
            database.insert(entry)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAddNewEntry() {
        newThing.startTimeMilli = timeToDateTime(_startTime.value)
        newThing.endTimeMilli = timeToDateTime(_endTime.value)


        viewModelScope.launch {
            insert(newThing)
        }
    }
}