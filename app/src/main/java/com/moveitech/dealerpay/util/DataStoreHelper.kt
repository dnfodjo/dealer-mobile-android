package com.moveitech.dealerpay.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreHelper @Inject constructor(private val dataStore: DataStore<Preferences>) {


    val isLogin: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGIN] ?: false
    }


    suspend fun saveIsLogin(isLogin: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGIN] = isLogin
        }
    }



    suspend fun clear() {
        dataStore.edit {
            it.clear()


        }
    }


    companion object {
        val IS_LOGIN = booleanPreferencesKey(name = "isLogin")
        const val DATA_STORE_NAME = "dealer_pay_datastore"

    }

}


