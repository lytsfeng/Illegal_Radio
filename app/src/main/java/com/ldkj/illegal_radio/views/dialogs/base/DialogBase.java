package com.ldkj.illegal_radio.views.dialogs.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by john on 15-3-10.
 */
public class DialogBase extends Dialog {
    public interface DialogCallBack<T> {
         void DialogBack(T t, int p_ResId, Boolean isInit);
    }
    public DialogBase(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
