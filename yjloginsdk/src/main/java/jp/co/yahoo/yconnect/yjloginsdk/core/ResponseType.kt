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

/**
 * Yahoo! ID連携の認可レスポンスとして取得するパラメーターを指定するために、認可リクエストに設定する値。
 *
 * @property value 認可リクエストにおけるクエリパラメータ名
 */
enum class ResponseType(val value: String) {
    /**
     * Authorization Codeのみを取得。
     */
    CODE("code"),
}
