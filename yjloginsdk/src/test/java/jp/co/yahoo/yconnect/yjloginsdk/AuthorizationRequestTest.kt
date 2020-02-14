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

package jp.co.yahoo.yconnect.yjloginsdk

import com.google.common.truth.Truth.assertThat
import jp.co.yahoo.yconnect.yjloginsdk.core.AuthorizationRequest
import jp.co.yahoo.yconnect.yjloginsdk.core.CodeChallengeMethod
import jp.co.yahoo.yconnect.yjloginsdk.core.Display
import jp.co.yahoo.yconnect.yjloginsdk.core.OptionalParameters
import jp.co.yahoo.yconnect.yjloginsdk.core.Prompt
import jp.co.yahoo.yconnect.yjloginsdk.core.ResponseType
import jp.co.yahoo.yconnect.yjloginsdk.core.Scope
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class AuthorizationRequestTest(
    private val clientId: String,
    private val redirectUri: String,
    private val responseType: ResponseType,
    private val scope: Set<Scope>,
    private val nonce: String,
    private val codeChallenge: String,
    private val codeChallengeMethod: CodeChallengeMethod,
    private val state: String,
    private val optionalParameters: OptionalParameters?,
    private val expected: String
) {

    private lateinit var authorizationRequest: AuthorizationRequest

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): List<Array<out Any?>> {
            return listOf(
                // シンプル
                arrayOf(
                    "a", "b", ResponseType.CODE, setOf(Scope.OPENID), "c", "d", CodeChallengeMethod.PLAIN, "e", null,
                    "https://auth.login.yahoo.co.jp/yconnect/v2/authorization?client_id=a&redirect_uri=b&response_type=code&scope=openid&nonce=c&code_challenge=d&code_challenge_method=plain&state=e"
                ),
                // URLエンコードが必要な文字列を含む(スペース)
                arrayOf(
                    "a", "yj-example:/", ResponseType.CODE, setOf(Scope.OPENID, Scope.PROFILE), "c", "d", CodeChallengeMethod.PLAIN, "e", null,
                    "https://auth.login.yahoo.co.jp/yconnect/v2/authorization?client_id=a&redirect_uri=yj-example%3A%2F&response_type=code&scope=openid%20profile&nonce=c&code_challenge=d&code_challenge_method=plain&state=e"
                ),
                // フルパラメータ
                arrayOf(
                    "a", "b", ResponseType.CODE, setOf(Scope.OPENID), "c", "d", CodeChallengeMethod.PLAIN, "e",
                    OptionalParameters(
                        bail = true,
                        display = Display.INAPP,
                        maxAge = 60,
                        prompts = setOf(Prompt.LOGIN)
                    ),
                    "https://auth.login.yahoo.co.jp/yconnect/v2/authorization?client_id=a&redirect_uri=b&response_type=code&scope=openid&nonce=c&code_challenge=d&code_challenge_method=plain&state=e&bail=1&display=inapp&max_age=60&prompt=login"
                ),
                // additionalParameters
                arrayOf(
                    "a", "b", ResponseType.CODE, setOf(Scope.OPENID), "c", "d", CodeChallengeMethod.PLAIN, "e",
                    OptionalParameters(
                        bail = true,
                        display = Display.INAPP,
                        maxAge = 60,
                        prompts = setOf(Prompt.LOGIN),
                        additionalParameters = mapOf("key" to "value")
                    ),
                    "https://auth.login.yahoo.co.jp/yconnect/v2/authorization?client_id=a&redirect_uri=b&response_type=code&scope=openid&nonce=c&code_challenge=d&code_challenge_method=plain&state=e&bail=1&display=inapp&max_age=60&prompt=login&key=value"
                ),
                // additionalParametersによる既存のパラメータの上書き
                arrayOf(
                    "a", "b", ResponseType.CODE, setOf(Scope.OPENID), "c", "d", CodeChallengeMethod.PLAIN, "e",
                    OptionalParameters(
                        additionalParameters = mapOf("scope" to "openid additional_scope")
                    ),
                    "https://auth.login.yahoo.co.jp/yconnect/v2/authorization?client_id=a&redirect_uri=b&response_type=code&scope=openid%20additional_scope&nonce=c&code_challenge=d&code_challenge_method=plain&state=e"
                )
            )
        }
    }

    @Before
    fun setUp() {
        authorizationRequest =
            AuthorizationRequest(
                clientId,
                redirectUri,
                responseType,
                scope,
                nonce,
                codeChallenge,
                codeChallengeMethod,
                state,
                optionalParameters
            )
    }

    @Test
    fun generate() {
        val url = authorizationRequest.generate()
        assertThat(url.toString()).isEqualTo(expected)
    }
}
