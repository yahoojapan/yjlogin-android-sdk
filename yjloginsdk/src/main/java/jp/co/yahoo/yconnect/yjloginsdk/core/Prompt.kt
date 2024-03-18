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
 * ユーザーに強制させたいアクションとして、認可リクエストに設定する値。
 *
 * @property value 認可リクエストにおけるクエリパラメータ名
 */
enum class Prompt(val value: String) {
    /**
     * 同意を要求。
     */
    CONSENT("consent"),

    /**
     * 再認証を要求。
     */
    LOGIN("login"),

    /**
     * 画面を非表示。
     */
    NONE("none"),

    /**
     * ID切り替えを強制。
     */
    SELECT_ACCOUNT("select_account"),
}
