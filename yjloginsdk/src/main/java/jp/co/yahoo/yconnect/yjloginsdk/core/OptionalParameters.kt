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
 * 認可リクエスト時に指定する任意パラメーター。
 *
 * @property bail 同意画面で「同意しない」ボタンをクリックした際の遷移先
 * @property display ログイン画面と同意画面で表示するページ種類
 * @property maxAge 最大認証経過時間。指定された秒数よりも認証日時が経過していた場合は再認証を要求
 * @property prompts ユーザーに強制させたいアクション
 * @property additionalParameters その他未定義のパラメーター。定義されていないパラメーターを指定したい場合に使用
 */
class OptionalParameters(
    private var bail: Boolean = false,
    private var display: Display? = null,
    private var maxAge: Int? = null,
    private var prompts: Set<Prompt>? = null,
    private var additionalParameters: Map<String, String>? = null
) {
    companion object {
        private const val PARAM_BAIL = "bail"
        private const val PARAM_DISPLAY = "display"
        private const val PARAM_MAX_AGE = "max_age"
        private const val PARAM_PROMPT = "prompt"
    }

    internal fun generate(): Map<String, String> {
        val parameters = mutableMapOf<String, String>()

        if (bail) { parameters[PARAM_BAIL] = "1" }

        display?.let { parameters[PARAM_DISPLAY] = it.value }

        maxAge?.let { parameters[PARAM_MAX_AGE] = maxAge.toString() }

        prompts?.let { prompts ->
            parameters[PARAM_PROMPT] = prompts.joinToString(" ") { it.value }
        }

        additionalParameters?.forEach { parameters[it.key] = it.value }

        return parameters
    }
}
