package com.example.apptest.viewModel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.fcmToken
import com.example.apptest.activities.Utils.Companion.jsonObject
import com.example.apptest.api.Api
import com.example.apptest.model.login.LoginData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel : ViewModel() {
    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,        // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState>
        get() = _authenticationState

    init {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    private var _email = MutableLiveData<String>()
    private var _password = MutableLiveData<String>()

    private val _userData = MutableLiveData<List<LoginData>>()
    val userData : LiveData<List<LoginData>>
    get() = _userData

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun createPost(email: String?, password: String?) {
        _email.value = email
        _password.value = password
        fcmToken = Utils.randomString(10)
        jsonObject.addProperty("EmailIdPhone", _email.value)
        jsonObject.addProperty("PWD", _password.value)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", Utils.deviceId)
        jsonObject.addProperty("FCMToken", fcmToken)

        coroutineScope.launch {
            contentType["Content-Type"] = "application/json"
            val loginDeferred = Api.RETROFIT_SERVICE.loginAsync(Utils.clientKey, contentType, jsonObject)
            try {
                val result = loginDeferred.await()
                if (result.Success) {
                    Timber.d("token ========> $result")
                    /*for (data in result.Data) {
                        _userData.value = data.EmailId
                        Token.value = data.Token
                        Phone.value = data.Phone
                        UserName.value = data.UserName
                        ResponseMessage.value = data.ResponseMessage
                    }*/
                    _userData.value = result.Data
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    Timber.d("Error ========>${result.Message}")
                    _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                }
            } catch (t: Throwable) {
                Timber.d("error while call =====> ${t.message.toString()}")
            }
        }
    }

    fun refuseAuthentication() {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }
}

