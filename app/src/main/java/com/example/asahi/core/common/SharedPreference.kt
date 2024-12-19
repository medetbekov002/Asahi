package com.example.asahi.core.common

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreference @Inject constructor(private val pref: SharedPreferences) {

    fun setTokensExpired(isExpired: Boolean) {
        pref.edit().putBoolean(TOKENS_EXPIRED, isExpired).apply()
    }

    fun areTokensExpired(): Boolean {
        return pref.getBoolean(TOKENS_EXPIRED, false)
    }

    fun setLogout(isExpired: Boolean) {
        pref.edit().putBoolean(LOGOUT_EXPIRED, isExpired).apply()
    }

    fun getLogout(): Boolean {
        return pref.getBoolean(LOGOUT_EXPIRED, true)
    }

    fun saveProfileImageUri(uri: String) {
        pref.edit().putString(PROFILE_IMAGE, uri).apply()
    }

    fun getProfileImageUri(): String? {
        return pref.getString(PROFILE_IMAGE, null)
    }

    fun saveGmail(gmail: String) {
        pref.edit().putString(GMAIL_KEY, gmail).apply()
    }

    fun getGmail(): String? {
        return pref.getString(GMAIL_KEY, null)
    }

    fun saveSessionId(sessionId: String) {
        pref.edit().putString(SESSION_ID, sessionId).apply()
    }

    fun getSessionId(): String? {
        return pref.getString(SESSION_ID, null)
    }

    fun saveRefreshToken(refreshToken: String) {
        pref.edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefresh(): String? {
        return pref.getString(REFRESH_TOKEN, null)
    }

    fun saveAccessToken(accessToken: String) {
        pref.edit().putString(ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(): String? {
        return pref.getString(ACCESS_TOKEN, null)
    }

    fun saveChaneEmailSessionId(sessionId: String) {
        pref.edit().putString(EMAIL_SESSION_ID, sessionId).apply()
    }

    fun getChaneEmailSessionId(): String? {
        return pref.getString(EMAIL_SESSION_ID, null)
    }

    fun saveFcmToken(token: String) {
        pref.edit().putString(FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? {
        return pref.getString(FCM_TOKEN, null)
    }

    companion object {
        const val PREF_NAME = "pref.name"
        const val GMAIL_KEY = "gmail.key"
        const val SESSION_ID = "session.id"
        const val REFRESH_TOKEN = "refresh.token"
        const val ACCESS_TOKEN = "access.token"
        const val EMAIL_SESSION_ID = "email.session.id"
        const val PROFILE_IMAGE = "profile_image_uri"
        const val TOKENS_EXPIRED = "tokens.expired"
        const val LOGOUT_EXPIRED = "logout.expired"
        const val FCM_TOKEN = "fcm.token"
    }

}