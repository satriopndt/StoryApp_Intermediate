package com.example.storyapp.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")
class LoginPreferences  private constructor(private val dataStore: DataStore<Preferences>){

    fun getSession(): Flow<AuthToken> {
        return dataStore.data.map { preference ->
            AuthToken(
                preference[TOKEN_KEY] ?:"",
                preference[USER_ID_KEY]?:"",
                preference[NAME_KEY]?:"",
                preference[IS_LOGIN]?:false
            )
        }
    }

    suspend fun saveSession(token: AuthToken) {
        dataStore.edit { preference ->
            preference[TOKEN_KEY] = token.token
            preference[USER_ID_KEY] = token.userId
            preference[NAME_KEY] = token.name
            preference[IS_LOGIN] = token.isLogin
        }
        Log.d("Token Saved","$token")
    }

    suspend fun removeTokenAuth(){
        dataStore.edit { preference->
            preference.clear()
        }
    }



    companion object {
        @Volatile
        private var instance: LoginPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token_key")
        private val USER_ID_KEY = stringPreferencesKey("id_key")
        private val NAME_KEY = stringPreferencesKey("name_key")
        private val IS_LOGIN = booleanPreferencesKey("isLogin_key")


        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return instance ?: synchronized(this) {
                instance ?: LoginPreferences(dataStore)
                    .also { instance = it }
            }
        }
    }

}