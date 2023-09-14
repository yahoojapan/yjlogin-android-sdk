/*
 * © 2023 LY Corporation
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
import android.net.Uri
import jp.co.yahoo.yconnect.yjloginsdk.util.Logger
import jp.co.yahoo.yconnect.yjloginsdk.util.base64UrlSafe
import jp.co.yahoo.yconnect.yjloginsdk.util.generateRandomByteArray

/**
 * ログインマネージャー。
 * Yahoo! ID連携のクライアントに関する設定をし、認可リクエストを実行した結果を返す。
 */
object LoginManager {
    private var configuration: LoginConfiguration? = null
    private var listener: LoginListener? = null
    internal var process: ILoginProcess? = null

    private val logger = Logger.LoggerProvider.provide()

    /**
     * ログインマネージャーの設定を行う。
     *
     * @param clientId アプリケーション登録時に発行したClient ID
     * @param redirectUri アプリケーション登録時に設定したフルURLもしくはカスタムURIスキーム
     */
    fun setup(
        clientId: String,
        redirectUri: Uri
    ) {
        configuration = LoginConfiguration(clientId, redirectUri)
    }

    /**
     * Yahoo! ID連携でログインを行う。
     *
     * @param context ログイン画面を表示するためのContext
     * @param scopes 要求するスコープのセット。OPENIDは必須
     * @param nonce リプレイアタック対策のパラメーター
     * @param codeChallenge PKCEのパラメーター
     * @param codeChallengeMethod codeChallengeの方式
     * @param optionalParameters 認可リクエスト時に指定する任意パラメーター
     */
    fun login(
        context: Context,
        scopes: Set<Scope>,
        nonce: String,
        codeChallenge: String,
        codeChallengeMethod: CodeChallengeMethod = CodeChallengeMethod.S256,
        optionalParameters: OptionalParameters? = null
    ) {
        login(
            context,
            scopes,
            nonce,
            codeChallenge,
            codeChallengeMethod,
            optionalParameters,
            listener
        )
    }

    internal fun login(
        context: Context,
        scopes: Set<Scope>,
        nonce: String,
        codeChallenge: String,
        codeChallengeMethod: CodeChallengeMethod,
        optionalParameters: OptionalParameters?,
        listener: LoginListener?
    ) {
        val configuration = configuration
            ?: throw RuntimeException("[yjloginsdk] Please call setup function before login.")

        logger.d("Begin to login")
        listener?.onLoginStart()

        process?.let {
            if (it.isBrowserTab) {
                logger.d("Login process running")
                listener?.onLoginFailure(LoginError.ProcessRunning)
                return@login
            }
        }

        val issuer = configuration.issuer?.toString() ?: run { Constant.DEFAULT_ISSUER }

        val url = AuthorizationRequest(
            configuration.clientId,
            configuration.redirectUri.toString(),
            ResponseType.CODE,
            scopes,
            nonce,
            codeChallenge,
            codeChallengeMethod,
            generateRandomByteArray(32).base64UrlSafe(),
            optionalParameters,
            issuer
        ).generate()

        process = LoginProcess(context, url, configuration.forceOpenCustomTabs, onFinish = {
            when (it) {
                is SealedLoginResult.Success -> {
                    logger.d("login success! ${it.loginResult}")
                    listener?.onLoginSuccess(it.loginResult)
                }

                is SealedLoginResult.Failure -> {
                    logger.d("login failed! ${it.loginError}")
                    listener?.onLoginFailure(it.loginError)
                }
            }
            process = null
        })

        process?.start()
    }

    /**
     * ログイン処理の通知のためのリスナーを保持する。
     *
     * @param listener 通知先のリスナー
     */
    fun setLoginListener(listener: LoginListener) {
        this.listener = listener
    }

    /**
     * ログイン処理の通知のためのリスナーを破棄する。
     */
    fun removeLoginListener() {
        listener = null
    }

    /**
     * Issuerを設定する。
     *
     * @param issuer Issuer
     */
    fun setIssuer(issuer: Uri) {
        if (!issuer.authority!!.endsWith("yahoo.co.jp")) {
            throw RuntimeException("[yjloginsdk] Please set valid issuer.")
        }

        if (issuer.path != "/yconnect/v2") {
            throw RuntimeException("[yjloginsdk] Please set valid issuer.")
        }

        configuration?.issuer = issuer
    }

    /**
     * ログイン時のCustom Tabsの利用を設定する。
     *
     * @param forceOpenCustomTabs trueならログイン時に強制的にCustom Tabsを利用する
     */
    fun setForceOpenCustomTabs(forceOpenCustomTabs: Boolean) {
        configuration?.forceOpenCustomTabs = forceOpenCustomTabs
    }
}
