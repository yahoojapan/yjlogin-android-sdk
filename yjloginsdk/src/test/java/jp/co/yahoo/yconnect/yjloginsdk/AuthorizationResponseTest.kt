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

package jp.co.yahoo.yconnect.yjloginsdk

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import jp.co.yahoo.yconnect.yjloginsdk.core.AuthorizationResponse
import jp.co.yahoo.yconnect.yjloginsdk.core.ErrorResponseDetail
import jp.co.yahoo.yconnect.yjloginsdk.core.ErrorResponseReason
import jp.co.yahoo.yconnect.yjloginsdk.core.InvalidResponseReason
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginError
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginResult
import jp.co.yahoo.yconnect.yjloginsdk.core.SealedLoginResult
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
internal class AuthorizationResponseTest(
    private val callbackUrl: String,
    private val state: String?,
    private val expected: SealedLoginResult
) {

    private lateinit var authorizationResponse: AuthorizationResponse

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun data(): List<Array<out Any?>> {
            return listOf(
                // 正常系
                arrayOf(
                    "yj-example://?code=a&state=b",
                    "b",
                    SealedLoginResult.Success(
                        LoginResult(
                            "a",
                            "b"
                        )
                    )
                ),
                // 正常系(android-app://をredirect_uriに指定)
                arrayOf(
                    "yj-example:///?code=a&state=b",
                    "b",
                    SealedLoginResult.Success(
                        LoginResult(
                            "a",
                            "b"
                        )
                    )
                ),
                // コールバックURLにstateがない場合
                arrayOf(
                    "yj-example:///?code=a",
                    "b",
                    SealedLoginResult.Failure(
                        LoginError.InvalidResponse(
                            InvalidResponseReason.INVALID_STATE))
                ),
                // リクエストにつんだstateがない場合
                arrayOf(
                    "yj-example:///?code=a&state=b",
                    null,
                    SealedLoginResult.Failure(
                        LoginError.InvalidResponse(
                            InvalidResponseReason.INVALID_STATE))
                ),
                // stateの値が異なる場合
                arrayOf(
                    "yj-example:///?code=a&state=b",
                    "c",
                    SealedLoginResult.Failure(
                        LoginError.InvalidResponse(
                            InvalidResponseReason.INVALID_STATE))
                ),
                // クエリパラメータがない場合
                arrayOf(
                    "yj-example:///",
                    "state",
                    SealedLoginResult.Failure(
                        LoginError.InvalidResponse(
                            InvalidResponseReason.NO_QUERY_PARAMETERS))
                ),
                // stateのみ返却されている場合(bail=1をつんで同意しないを選択)
                arrayOf(
                    "yj-example:///?state=a",
                    "a",
                    SealedLoginResult.Failure(
                        LoginError.InvalidResponse(
                            InvalidResponseReason.CONSENT_DENIED))
                ),
                // 定義済みのエラーレスポンスが返却される場合
                arrayOf(
                    "yj-example:///?state=a&error=invalid_request&error_description=Unsupported%20response_type%20value&error_code=1000",
                    "a",
                    SealedLoginResult.Failure(
                        LoginError.ErrorResponse(
                            ErrorResponseReason.INVALID_REQUEST,
                            ErrorResponseDetail(
                                "invalid_request",
                                "Unsupported response_type value",
                                1000
                            )
                        )
                    )
                ),
                // 未定義のエラーレスポンスが返却される場合
                arrayOf(
                    "yj-example:///?state=a&error=undefined&error_description=Undefined%20error&error_code=9999",
                    "a",
                    SealedLoginResult.Failure(
                        LoginError.ErrorResponse(
                            ErrorResponseReason.UNDEFINED_ERROR,
                            ErrorResponseDetail(
                                "undefined",
                                "Undefined error",
                                9999
                            )
                        )
                    )
                )
            )
        }
    }

    @Before
    fun setUp() {
        authorizationResponse =
            AuthorizationResponse(
                Uri.parse(callbackUrl)
            )
    }

    @Test
    fun validate() {
        when (val sealedLoginResult = authorizationResponse.validate(state)) {
            is SealedLoginResult.Success -> {
                assertThat(sealedLoginResult).isEqualTo(expected)
            }

            is SealedLoginResult.Failure -> {
                val expectedLoginError = (expected as SealedLoginResult.Failure).loginError

                when (val loginError = sealedLoginResult.loginError) {
                    is LoginError.ErrorResponse -> {
                        val errorResponse = expectedLoginError as LoginError.ErrorResponse
                        assertThat(loginError.reason).isEqualTo(errorResponse.reason)
                        assertThat(loginError.detail).isEqualTo(errorResponse.detail)
                    }

                    is LoginError.InvalidResponse -> {
                        val invalidResponse = expectedLoginError as LoginError.InvalidResponse
                        assertThat(loginError.reason).isEqualTo(invalidResponse.reason)
                    }

                    else -> {
                        assertThat(loginError).isEqualTo(expectedLoginError)
                    }
                }
            }

            else -> {
                fail("return unexpected result")
            }
        }
    }
}
