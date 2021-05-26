package com.github.code.gambit.utility.sharedpreference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

class LastEvaluatedKeyManager
constructor(@ApplicationContext context: Context) : PreferenceManager(context) {

    fun putLastEvalKey(lastEvaluatedKey: String, keyType: KeyType) {
        when (keyType) {
            KeyType.FILE -> put(Key.FILE_LEK, lastEvaluatedKey)
            KeyType.URL -> put(Key.URL_LEK, lastEvaluatedKey)
        }
    }

    fun getLastEvalKey(keyType: KeyType): String {
        return when (keyType) {
            KeyType.FILE -> get(Key.FILE_LEK)
            KeyType.URL -> get(Key.URL_LEK)
        }
    }

    enum class KeyType { FILE, URL }
}
