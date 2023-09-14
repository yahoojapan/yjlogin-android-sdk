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

/**
 * ログイン画面と同意画面で表示するページ種類として、認可リクエストに設定する値。
 * 初期値は`page`。
 *
 * @property value 認可リクエストにおけるクエリパラメータ名
 */
enum class Display(val value: String) {
    /**
     * ネイティブアプリ版ページ。
     */
    INAPP("inapp"),

    /**
     * UserAgentに適したページ。
     * 初期値。
     */
    PAGE("page"),

    /**
     * ポップアップ版ページ。
     */
    POPUP("popup"),

    /**
     * スマートフォン版ページ。
     */
    TOUCH("touch"),
}
