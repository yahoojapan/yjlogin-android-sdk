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

package jp.co.yahoo.yconnect.yjloginsdk.util

import android.util.Log

internal class Logger {
    interface Logger {
        fun d(message: String)
        fun d(message: String, e: Exception?)
        fun i(message: String)
        fun i(message: String, e: Exception?)
        fun e(message: String)
        fun e(message: String, e: Exception?)
    }

    class YJLoginLogger : Logger {
        override fun d(message: String) {
            Log.d(TAG, createMessage(message))
        }

        override fun d(message: String, e: java.lang.Exception?) {
            Log.d(TAG, createMessage(message))
        }

        override fun i(message: String) {
            Log.i(TAG, createMessage(message))
        }

        override fun i(message: String, e: java.lang.Exception?) {
            Log.i(TAG, createMessage(message))
        }

        override fun e(message: String) {
            Log.e(TAG, createMessage(message))
        }

        override fun e(message: String, e: java.lang.Exception?) {
            Log.e(TAG, createMessage(message))
        }

        private fun createMessage(message: String): String {
            if (Throwable().stackTrace.size > 2) {
                val stackTraceElement = Throwable().stackTrace[2]
                return stackTraceElement.className +
                        "." + stackTraceElement.methodName +
                        ":" + stackTraceElement.lineNumber.toString() +
                        " " + message
            }

            return message
        }

        companion object {
            private const val TAG = "yjloginsdk"
        }
    }

    class DummyLogger : Logger {
        override fun d(message: String) {}
        override fun d(message: String, e: java.lang.Exception?) {}
        override fun i(message: String) {}
        override fun i(message: String, e: java.lang.Exception?) {}
        override fun e(message: String) {}
        override fun e(message: String, e: java.lang.Exception?) {}
    }

    object LoggerProvider {
        private var logger: Logger = YJLoginLogger()

        fun setLogger(logger: Logger) {
            LoggerProvider.logger = logger
        }

        fun provide(): Logger {
            return logger
        }
    }
}
