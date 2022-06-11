package com.moveitech.dealerpay.database

import androidx.room.RoomDatabase
import com.moveitech.dealerpay.database.AppDao

//@Database(entities = [Attendance::class,Location::class], version = 1)
abstract class DealerPayDB : RoomDatabase() {



    abstract fun dbDao(): AppDao




}