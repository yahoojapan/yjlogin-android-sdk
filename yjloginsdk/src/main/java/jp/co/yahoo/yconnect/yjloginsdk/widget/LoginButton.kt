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

package jp.co.yahoo.yconnect.yjloginsdk.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.setPadding
import jp.co.yahoo.yconnect.yjloginsdk.R
import jp.co.yahoo.yconnect.yjloginsdk.core.CodeChallengeMethod
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginListener
import jp.co.yahoo.yconnect.yjloginsdk.core.LoginManager
import jp.co.yahoo.yconnect.yjloginsdk.core.OptionalParameters
import jp.co.yahoo.yconnect.yjloginsdk.core.Scope

/**
 * ログインボタンのデザインの種別。
 *
 * @property index attrs.xmlに定義されているインデックス
 */
enum class LoginButtonType(val index: Int) {
    /**
     * アイコンとテキスト、白背景。
     */
    BUTTON_WHITE(0),

    /**
     * アイコンとテキスト、赤背景。
     */
    BUTTON_RED(1),

    /**
     * アイコンのみ、白背景。
     */
    ICON_WHITE(2),

    /**
     * アイコンのみ、赤背景。
     */
    ICON_RED(3);

    internal companion object {
        fun fromInt(index: Int): LoginButtonType {
            return values().singleOrNull { it.index == index } ?: default()
        }

        fun default(): LoginButtonType = BUTTON_WHITE
    }
}

/**
 * ログインボタン。
 * Yahoo! JAPAN IDログインボタン デザインガイドラインに準拠。
 *
 * @see <a href="https://developer.yahoo.co.jp/yconnect/loginbuttons.html">Yahoo! JAPAN IDログインボタン デザインガイドライン</a>
 */
class LoginButton : AppCompatImageButton {
    /**
     * 要求するスコープのセット。OPENIDは必須。
     */
    var scopes: Set<Scope>? = null

    /**
     * リプレイアタック対策のパラメーター。
     */
    var nonce: String? = null

    /**
     * PKCEのパラメーター。
     */
    var codeChallenge: String? = null

    /**
     * codeChallengeの方式。
     */
    var codeChallengeMethod: CodeChallengeMethod = CodeChallengeMethod.S256

    /**
     * 認可リクエスト時に指定する任意パラメーター。
     */
    var optionalParameters: OptionalParameters? = null

    /**
     * ログイン処理の通知のためのリスナー。
     */
    var listener: LoginListener? = null

    init {
        setOnClickListener {
            LoginManager.login(
                context,
                scopes!!,
                nonce!!,
                codeChallenge!!,
                codeChallengeMethod,
                optionalParameters,
                listener
            )
        }

        setOnTouchListener { v, event ->
            if (v is LoginButton) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.drawable.colorFilter =
                            PorterDuffColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                        v.invalidate()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.drawable.clearColorFilter()
                        v.invalidate()
                    }
                }
            }

            v.onTouchEvent(event)
        }
    }

    constructor(context: Context) : super(context) {
        setImage(LoginButtonType.default())
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val buttonType = getButtonType(context, attrs)
        setImage(buttonType)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        val buttonType = getButtonType(context, attrs)
        setImage(buttonType)
    }

    /**
     * ボタンの画像をセットする。
     *
     * @param type ボタンデザインの種別
     */
    fun setImage(type: LoginButtonType) {
        when (type) {
            LoginButtonType.BUTTON_WHITE -> setImageResource(R.drawable.btn_white)
            LoginButtonType.BUTTON_RED -> setImageResource(R.drawable.btn_red)
            LoginButtonType.ICON_WHITE -> setImageResource(R.drawable.ic_white)
            LoginButtonType.ICON_RED -> setImageResource(R.drawable.ic_red)
        }

        background = null
        setPadding(0)
    }

    private fun getButtonType(context: Context, attrs: AttributeSet?): LoginButtonType {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoginButton)
        val typeIndex = typedArray.getInt(R.styleable.LoginButton_button_type, 0)
        typedArray.recycle()
        return LoginButtonType.fromInt(typeIndex)
    }
}
