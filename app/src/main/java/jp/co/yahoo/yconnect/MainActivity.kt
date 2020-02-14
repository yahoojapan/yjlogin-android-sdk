/*
 * Copyright 2020 Yahoo Japan Corporation
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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewmodel.result.observe(this, Observer {
            result.text = it
        })

        loginButton.setOnClickListener {
            viewmodel.onClickLoginButton(this)
        }

        loginButtonWhite.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        loginButtonRed.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        loginIconWhite.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        loginIconRed.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }
    }
}
