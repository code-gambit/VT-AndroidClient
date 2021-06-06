package com.github.code.gambit.utility.sharedpreference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

class LastEvaluatedKeyManager
constructor(@ApplicationContext context: Context) : PreferenceManager(context) {

    fun putLastEvalKey(lastEvaluatedKey: String, keyType: KeyType) {
        when (keyType) {
            KeyType.FILE -> put(Key.FILE_LEK, lastEvaluatedKey)
            KeyType.URL -> put(Key.URL_LEK, lastEvaluatedKey)
            KeyType.FILTER_FILE -> put(Key.FILTER_FILE_LEK, lastEvaluatedKey)
            KeyType.SEARCH_FILE -> put(Key.SEARCH_FILE_LEK, lastEvaluatedKey)
        }
    }

    fun getLastEvalKey(keyType: KeyType): String {
        return when (keyType) {
            KeyType.FILE -> get(Key.FILE_LEK)
            KeyType.URL -> get(Key.URL_LEK)
            KeyType.FILTER_FILE -> get(Key.FILTER_FILE_LEK)
            KeyType.SEARCH_FILE -> get(Key.SEARCH_FILE_LEK)
        }
    }

    fun flush() {
        val empty = ""
        putLastEvalKey(empty, KeyType.FILE)
        putLastEvalKey(empty, KeyType.URL)
        putLastEvalKey(empty, KeyType.FILTER_FILE)
        putLastEvalKey(empty, KeyType.SEARCH_FILE)
    }

    enum class KeyType { FILE, URL, FILTER_FILE, SEARCH_FILE }
}
