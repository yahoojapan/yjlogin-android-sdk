/*
 * Copyright 2023 LY Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.yahoo.yconnect

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginError
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginListener
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginManager
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginResult
import jp.co.yahoo.yconnect.yjloginsdk.core.Scope

class MainViewModel(app: Application) : AndroidViewModel(app), LoginListener {
    private val context = app.applicationContext
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    // nonceとcode_challengeはサーバーで生成し、代入してください。
    val nonce = "<nonce>"
    val codeChallenge = "<code_challenge>"
    val scopes = setOf(Scope.OPENID)

    init {
        LoginManager.setup("<client_id>", "<redirect_uri>".toUri())
        LoginManager.setLoginListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        LoginManager.removeLoginListener()
    }

    fun onClickLoginButton(context: Context) {
        LoginManager.login(
            context,
            scopes,
            nonce,
            codeChallenge
        )
    }

    override fun onLoginStart() {
        val message = "Start to login"
        Log.d(this.javaClass.simpleName, message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoginSuccess(loginResult: LoginResult) {
        val messages = listOf("Login successful!", loginResult)
        Log.d(this.javaClass.simpleName, messages.joinToString(", "))
        _result.value = messages.joinToString("\n")
    }

    override fun onLoginFailure(loginError: LoginError) {
        val messages = mutableListOf("Login failed...")

        when (loginError) {
            is LoginError.ErrorResponse -> {
                messages.apply {
                    add(loginError.toString())
                    add("reason: ${loginError.reason.name}")
                    add("detail: ${loginError.detail}")
                }
            }

            is LoginError.InvalidResponse -> {
                messages.apply {
                    add(loginError.toString())
                    add("reason: ${loginError.reason.name}")
                }
            }

            else -> {
                messages.apply {
                    add(loginError.toString())
                }
            }
        }

        Log.d(this.javaClass.simpleName, messages.joinToString(", "))
        _result.value = messages.joinToString("\n")
    }
}
