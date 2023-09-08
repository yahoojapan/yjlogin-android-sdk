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

/**
 * ログイン処理の結果として返却するエラー。
 */
sealed class LoginError {
    /**
     * ユーザ操作によってログインがキャンセルされた場合。
     */
    object UserCancel : LoginError()

    /**
     * ログイン処理がすでに実行中の場合。
     */
    object ProcessRunning : LoginError()

    /**
     * 認可サーバからエラーレスポンスが返却された場合。
     *
     * @property reason エラー種別
     * @property detail エラーレスポンスの詳細
     */
    class ErrorResponse(
        val reason: ErrorResponseReason,
        val detail: ErrorResponseDetail
    ) : LoginError()

    /**
     * 認可サーバから不正なレスポンスが返却された場合。
     *
     * @property reason エラー種別
     */
    class InvalidResponse(
        val reason: InvalidResponseReason
    ) : LoginError()

    /**
     * 未定義のエラー
     */
    object UndefinedError : LoginError()

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

/**
 * 認可サーバからエラーレスポンスとして返却されるエラーの種別。
 */
enum class ErrorResponseReason {
    ACCESS_DENIED,
    CONSENT_REQUIRED,
    INTERACTION_REQUIRED,
    INVALID_REQUEST,
    INVALID_SCOPE,
    LOGIN_REQUIRED,
    SERVER_ERROR,
    UNSUPPORTED_RESPONSE_TYPE,
    UNDEFINED_ERROR
}

/**
 * 不正なレスポンスの種別。
 */
enum class InvalidResponseReason {
    INVALID_STATE,
    CONSENT_DENIED,
    NO_QUERY_PARAMETERS,
}

/**
 * エラーレスポンスに含まれる値。
 *
 * @property error error
 * @property errorDescription error_description
 * @property errorCode error_code
 */
data class ErrorResponseDetail(
    val error: String,
    val errorDescription: String,
    val errorCode: Int
)
