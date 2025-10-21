/*
 * © 2024 LY Corporation. All Rights Reserved.
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

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jp.co.yahoo.yconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewmodel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // edge-to-edge表示対応
        enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { true })
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewmodel.result.observe(this, Observer {
            binding.result.text = it
        })

        binding.loginButton.setOnClickListener {
            viewmodel.onClickLoginButton(this)
        }

        binding.loginButtonWhite.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        binding.loginButtonRed.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        binding.loginIconWhite.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        binding.loginIconRed.also {
            it.scopes = viewmodel.scopes
            it.nonce = viewmodel.nonce
            it.codeChallenge = viewmodel.codeChallenge
            it.listener = viewmodel
        }

        // edge-to-edge表示対応のためのインセット設定
        setupWindowInsetsListener()
    }

    private fun setupWindowInsetsListener() {
        val statusBarBg = findViewById<View>(R.id.statusBarBackground)
        ViewCompat.setOnApplyWindowInsetsListener(statusBarBg) { v, windowInsets ->
            val bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            val lp = v.layoutParams as ViewGroup.MarginLayoutParams
            lp.height = bars.top
            lp.leftMargin = bars.left
            lp.rightMargin = bars.right
            v.layoutParams = lp
            windowInsets
        }
    }
}
