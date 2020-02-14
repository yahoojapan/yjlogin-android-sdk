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

package jp.co.yahoo.yconnect.yjloginsdk.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginError
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginManager
import jp.co.yahoo.yconnect.yjloginsdk.util.CustomTabsHelper
import jp.co.yahoo.yconnect.yjloginsdk.util.Logger

internal class LoginProcessActivity : AppCompatActivity() {
    private val logger = Logger.LoggerProvider.provide()
    private var isCustomTabsOpened = false
    private var url: String? = null
    private var customTabsPackageName: String? = null
    private var useCustomTabs = false
    private var onFinished: (() -> Unit)? = null

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_CUSTOM_TABS_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        private const val EXTRA_USE_CUSTOM_TABS = "EXTRA_USE_CUSTOM_TABS"

        private const val KEY_IS_CUSTOM_TABS_OPENED = "KEY_IS_CUSTOM_TABS_OPENED"
        private const val KEY_URL = "KEY_URL"
        private const val KEY_CUSTOM_TABS_PACKAGE_NAME = "KEY_CUSTOM_TABS_PACKAGE_NAME"
        private const val KEY_USE_CUSTOM_TABS = "KEY_USE_CUSTOM_TABS"

        fun createIntent(
            context: Context,
            url: String,
            packageName: String,
            useCustomTabs: Boolean = true
        ): Intent {
            return Intent(context, LoginProcessActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_CUSTOM_TABS_PACKAGE_NAME, packageName)
                putExtra(EXTRA_USE_CUSTOM_TABS, useCustomTabs)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            isCustomTabsOpened = it.getBoolean(KEY_IS_CUSTOM_TABS_OPENED)
            url = it.getString(KEY_URL)
            customTabsPackageName = it.getString(KEY_CUSTOM_TABS_PACKAGE_NAME)
            useCustomTabs = it.getBoolean(KEY_USE_CUSTOM_TABS)
        }
    }

    override fun onResume() {
        super.onResume()

        val useCustomTabs = intent.getBooleanExtra(EXTRA_USE_CUSTOM_TABS, false)

        if (useCustomTabs && !isCustomTabsOpened) {
            val url = intent.getStringExtra(EXTRA_URL)?.toUri()
                ?: run {
                    logger.e("EXTRA_URL not found")
                    finish { LoginManager.process?.fail(LoginError.UndefinedError) }
                    return
                }

            val customTabsPackageName = intent.getStringExtra(EXTRA_CUSTOM_TABS_PACKAGE_NAME)
                ?: run {
                    logger.e("EXTRA_CUSTOM_TABS_PACKAGE_NAME not found")
                    finish { LoginManager.process?.fail(LoginError.UndefinedError) }
                    return
                }

            logger.d("Launch Custom Tabs")

            this.isCustomTabsOpened = true
            this.url = url.toString()
            this.customTabsPackageName = customTabsPackageName
            this.useCustomTabs = useCustomTabs

            CustomTabsHelper.launch(this, customTabsPackageName, url)
            return
        }

        finish {
            val callbackUrl = intent.data
            callbackUrl?.let {
                LoginManager.process?.resume(it)
            } ?: run {
                LoginManager.process?.fail(LoginError.UserCancel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onFinished?.invoke()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(KEY_IS_CUSTOM_TABS_OPENED, isCustomTabsOpened)
            putString(KEY_URL, url)
            putString(KEY_CUSTOM_TABS_PACKAGE_NAME, customTabsPackageName)
            putBoolean(KEY_USE_CUSTOM_TABS, useCustomTabs)
        }
    }

    private fun finish(onFinished: () -> Unit) {
        this.onFinished = onFinished
        finish()
    }
}
