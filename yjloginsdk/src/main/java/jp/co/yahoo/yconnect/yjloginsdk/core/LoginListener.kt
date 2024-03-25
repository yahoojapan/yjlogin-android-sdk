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

package jp.co.yahoo.yconnect.yjloginsdk.core

/**
 * ログイン処理の開始、終了等を通知するリスナー。
 */
interface LoginListener {
    /**
     * ログイン開始を通知。
     */
    fun onLoginStart() {}

    /**
     * ログイン成功を通知。
     *
     * @param loginResult ログイン成功時に返却するデータ
     */
    fun onLoginSuccess(loginResult: LoginResult)

    /**
     * ログイン失敗を通知。
     *
     * @param loginError ログイン失敗時に返却するエラー
     */
    fun onLoginFailure(loginError: LoginError)
}
