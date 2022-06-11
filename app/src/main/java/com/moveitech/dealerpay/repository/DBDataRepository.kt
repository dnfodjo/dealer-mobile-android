package com.moveitech.dealerpay.repository

import com.moveitech.dealerpay.database.AppDao
import javax.inject.Inject

class DBDataRepository @Inject constructor(private var dao: AppDao) {


//    fun getLocations():LiveData<List<Location>>
//    {
//        return  dao.getLocations().asLiveData()
//    }

//    suspend fun saveLocation(location: Location)=dao.insertLocation(location)
//    suspend fun saveAttendance(attendance: Attendance)=dao.insertAttendance(attendance)
}