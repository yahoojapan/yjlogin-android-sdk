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

package jp.co.yahoo.yconnect.yjloginsdk.core

import android.net.Uri

internal class AuthorizationRequest(
    val clientId: String,
    val redirectUri: String,
    val responseType: ResponseType,
    val scope: Set<Scope>,
    val nonce: String,
    val codeChallenge: String,
    val codeChallengeMethod: CodeChallengeMethod,
    val state: String,
    val optionalParameters: OptionalParameters?
) {
    companion object {
        private const val ENDPOINT_URL = "https://auth.login.yahoo.co.jp/yconnect/v2/authorization"

        private const val PARAM_CLIENT_ID = "client_id"
        private const val PARAM_REDIRECT_URI = "redirect_uri"
        private const val PARAM_RESPONSE_TYPE = "response_type"
        private const val PARAM_SCOPE = "scope"
        private const val PARAM_NONCE = "nonce"
        private const val PARAM_CODE_CHALLENGE = "code_challenge"
        private const val PARAM_CODE_CHALLENGE_METHOD = "code_challenge_method"
        private const val PARAM_STATE = "state"
    }

    fun generate(): Uri {
        val requiredParameters = mutableMapOf(
            PARAM_CLIENT_ID to clientId,
            PARAM_REDIRECT_URI to redirectUri,
            PARAM_RESPONSE_TYPE to responseType.value,
            PARAM_SCOPE to scope.joinToString(" ") { it.value },
            PARAM_NONCE to nonce,
            PARAM_CODE_CHALLENGE to codeChallenge,
            PARAM_CODE_CHALLENGE_METHOD to codeChallengeMethod.value,
            PARAM_STATE to state
        )

        val allParameters = mutableMapOf<String, String>()
        allParameters.putAll(requiredParameters)
        optionalParameters?.let {
            allParameters.putAll(optionalParameters.generate())
        }

        val builder = Uri.parse(ENDPOINT_URL).buildUpon()

        allParameters.forEach {
            builder.appendQueryParameter(it.key, it.value)
        }

        return builder.build()
    }
}
