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

package jp.co.yahoo.yconnect.yjloginsdk.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal object CustomTabsHelper {

    private val BROWSER_INTENT = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("http://www.example.com")
    )

    private val logger = Logger.LoggerProvider.provide()

    private enum class CustomTabsPackages(
        val packageName: String,
        val hash: String
    ) {
        CHROME_STABLE(
            "com.android.chrome",
            "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c" +
                    "4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg=="
        ),
        CHROME_BETA(
            "com.chrome.beta",
            "ZZTQrvpldI8bmSdc8TKK3KISErF8zy-nMp269KAuPxyv" +
                    "Vz7BqgczKtS90pKGEPV8eVOIRqFDaRe4aDie4lCTpw=="
        ),
        CHROME_DEV(
            "com.chrome.dev",
            "JlOLOTFn6OFBFWuWQJYJ8h_aozEN7_zLFTfioXiXTrU6Y" +
                    "aft4cdEbdpkoJIvmB7Gv2HpHu6QOz-XIaXybtzL7A=="
        ),
        FIREFOX(
            "org.mozilla.firefox",
            "2gCe6pR_AO_Q2Vu8Iep-4AsiKNnUHQxu0FaDHO_qa178G" +
                    "ByKybdT_BuE8_dYk99G5Uvx_gdONXAOO2EaXidpVQ=="
        ),
    }

    fun launch(context: Context, packageName: String, requestUrl: Uri, forceOpenCustomTabs: Boolean) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        if (forceOpenCustomTabs) {
            customTabsIntent.intent.setPackage(packageName)
        }
        customTabsIntent.launchUrl(context, requestUrl)
    }

    fun getPackageName(context: Context): String? {
        val pm = context.packageManager

        val installedCustomTabsBrowsers = getInstalledCustomTabsBrowserPackageNames(pm)

        logger.d("Installed Custom Tabs Browsers: $installedCustomTabsBrowsers")

        // CustomTabsの一覧にデフォルトブラウザがある場合
        installedCustomTabsBrowsers.firstOrNull {
            it == getDefaultBrowserPackageName(pm) && isMatchHash(pm, it)
        }?.let {
            return it
        }

        // CustomTabsの一覧にChrome, Firefoxがある場合
        CustomTabsPackages.values().map { it.packageName }.firstOrNull {
            installedCustomTabsBrowsers.contains(it) && isMatchHash(pm, it)
        }?.let {
            return it
        }

        return installedCustomTabsBrowsers.firstOrNull()
    }

    private fun getInstalledCustomTabsBrowserPackageNames(pm: PackageManager): List<String> {
        val serviceIntent = Intent(ACTION_CUSTOM_TABS_CONNECTION)
        return pm.queryIntentServices(serviceIntent, 0).map { it.serviceInfo.packageName }
    }

    private fun getDefaultBrowserPackageName(pm: PackageManager): String? {
        return pm.resolveActivity(BROWSER_INTENT, 0)?.activityInfo?.packageName
    }

    private fun isMatchHash(pm: PackageManager, packageName: String): Boolean {
        getSignatures(pm, packageName).forEach { signature ->
            val hash = generateHash(signature)
                ?: return false
            val expectedHash = CustomTabsPackages.values().firstOrNull {
                it.packageName == packageName
            }?.hash
                ?: return false

            if (hash == expectedHash) {
                return true
            }
        }

        return false
    }

    private fun getSignatures(pm: PackageManager, packageName: String): Set<Signature> {
        return pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            .signatures
            ?.toSet() ?: emptySet()
    }

    private fun generateHash(signature: Signature): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-512")
            val hashBytes = digest.digest(signature.toByteArray())
            Base64.encodeToString(hashBytes, Base64.URL_SAFE or Base64.NO_WRAP)
        } catch (e: NoSuchAlgorithmException) {
            logger.e("Cannot generate hash of package signature", e)
            null
        }
    }
}
