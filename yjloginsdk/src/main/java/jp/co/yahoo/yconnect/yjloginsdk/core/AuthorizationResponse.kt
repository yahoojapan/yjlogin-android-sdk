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

import android.net.Uri
import java.net.URI
import jp.co.yahoo.yconnect.yjloginsdk.util.Logger

internal class AuthorizationResponse(private val callbackUrl: Uri) {
    companion object {
        private val logger =
            Logger.LoggerProvider.provide()

        private const val PARAM_CODE = "code"
        private const val PARAM_STATE = "state"
        private const val PARAM_ERROR = "error"
        private const val PARAM_ERROR_DESCRIPTION = "error_description"
        private const val PARAM_ERROR_CODE = "error_code"
    }

    fun validate(requestState: String?): SealedLoginResult {
        val query = parseQueryParameters(callbackUrl)

        if (query.isEmpty()) {
            return SealedLoginResult.Failure(
                LoginError.InvalidResponse(InvalidResponseReason.NO_QUERY_PARAMETERS)
            )
        }

        return validate(query, requestState)
    }

    private fun parseQueryParameters(callbackUrl: Uri): Map<String, String> {
        val query = mutableMapOf<String, String>()

        callbackUrl.queryParameterNames?.forEach { key ->
            callbackUrl.getQueryParameter(key)?.let { value ->
                query[key] = value
            }
        }

        // NOTE: scheme:///~の場合クエリパラメータを取得できないため、独自にパースする
        if (query.isEmpty()) {
            val queryString = URI.create(callbackUrl.toString()).query
            queryString?.let { queryString ->
                val keyValues = queryString.split("&")
                keyValues.forEach {
                    val idx = it.indexOf("=")
                    val key = it.substring(0, idx)
                    val value = it.substring(idx + 1)
                    query[key] = value
                }
            }
        }

        return query
    }

    private fun validate(query: Map<String, String>, requestState: String?): SealedLoginResult {
        logger.d("Validate callback URL")
        // stateの整合性チェック
        validateState(query, requestState)?.let {
            return SealedLoginResult.Failure(it)
        }

        // エラーレスポンスチェック
        validateErrorResponse(query)?.let {
            return SealedLoginResult.Failure(it)
        }

        // code存在チェック
        // bail=1のとき
        val code = query[PARAM_CODE] ?: run {
            return SealedLoginResult.Failure(
                LoginError.InvalidResponse(InvalidResponseReason.CONSENT_DENIED)
            )
        }

        val responseState = query[PARAM_STATE]
        return SealedLoginResult.Success(
            LoginResult(code, responseState)
        )
    }

    private fun validateState(
        query: Map<String, String>,
        requestState: String?
    ): LoginError.InvalidResponse? {
        val responseState = query[PARAM_STATE]
        if (requestState == null || responseState == null || requestState != responseState) {
            return LoginError.InvalidResponse(InvalidResponseReason.INVALID_STATE)
        }

        return null
    }

    private fun validateErrorResponse(query: Map<String, String>): LoginError.ErrorResponse? {
        val error = query[PARAM_ERROR]
        val errorDescription = query[PARAM_ERROR_DESCRIPTION]
        val errorCode = query[PARAM_ERROR_CODE]?.toIntOrNull()

        if (error != null && errorDescription != null && errorCode != null) {
            ErrorResponseReason.values().firstOrNull { it.name.toLowerCase() == error }?.let {
                val detail = ErrorResponseDetail(error, errorDescription, errorCode)
                return LoginError.ErrorResponse(it, detail)
            }

            val detail = ErrorResponseDetail(error, errorDescription, errorCode)

            logger.e("Undefined error response: $detail")

            return LoginError.ErrorResponse(ErrorResponseReason.UNDEFINED_ERROR, detail)
        }

        return null
    }
}
