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
 * UserInfo APIから取得する属性情報として、認可リクエストに設定する値。
 *
 * @property value認可リクエストにおけるクエリパラメータ名
 * @see <a href="https://developer.yahoo.co.jp/yconnect/v2/userinfo.html">属性取得API（UserInfoAPI）</a>
 */
enum class Scope(val value: String) {
    /**
     * 住所。
     */
    ADDRESS("address"),

    /**
     * メールアドレス。
     */
    EMAIL("email"),

    /**
     * ユーザー識別子。
     */
    OPENID("openid"),

    /**
     * 名前、性別などのその他属性情報。
     */
    PROFILE("profile"),
}
