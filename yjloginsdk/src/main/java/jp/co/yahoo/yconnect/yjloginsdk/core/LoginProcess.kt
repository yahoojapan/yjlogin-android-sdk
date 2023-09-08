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

package jp.co.yahoo.yconnect.yjloginsdk.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import jp.co.yahoo.yconnect.yjloginsdk.activity.LoginProcessActivity
import jp.co.yahoo.yconnect.yjloginsdk.util.CustomTabsHelper
import jp.co.yahoo.yconnect.yjloginsdk.util.Logger

internal class LoginProcess(
    private val context: Context,
    private val url: Uri,
    private val forceOpenCustomTabs: Boolean,
    override val onFinish: ((SealedLoginResult) -> Unit)?
) : ILoginProcess {
    private val logger = Logger.LoggerProvider.provide()
    override var isBrowserTab: Boolean = false

    override fun start() {
        val packageName = CustomTabsHelper.getPackageName(context)
        logger.d("Authorization URL: $url")
        logger.d("Package Name to open Custom Tabs: $packageName")

        val intent = packageName?.let {
            isBrowserTab = true
            LoginProcessActivity.createIntent(
                context = context,
                url = url.toString(),
                packageName = packageName,
                forceOpenCustomTabs = forceOpenCustomTabs
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } ?: run {
            Intent(Intent.ACTION_VIEW, url)
        }

        context.startActivity(intent)
    }

    override fun resume(callbackUrl: Uri) {
        logger.d("Callback URL: $callbackUrl")
        val response = AuthorizationResponse(callbackUrl)
        onFinish?.invoke(response.validate(url.getQueryParameter("state")))
    }

    override fun fail(loginError: LoginError) {
        onFinish?.invoke(SealedLoginResult.Failure(loginError))
    }
}
