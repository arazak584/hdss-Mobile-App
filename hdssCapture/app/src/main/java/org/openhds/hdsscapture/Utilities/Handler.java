package org.openhds.hdsscapture.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

public class Handler {

    @BindingAdapter("goneUnless")
    public static void goneUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    @InverseBindingAdapter(attribute = "selection")
    public static int getSelectItem(Spinner view) {
        int selection = 0;
        if (view != null) {
            final KeyValuePair kv = (KeyValuePair) view.getSelectedItem();
            selection = kv.codeValue;
        }
        return selection;
    }

    @BindingAdapter("selection")
    public static void setSelectItem(Spinner view, int selection) {
        for (int i = 0; i < view.getCount(); i++) {
            final KeyValuePair kv = (KeyValuePair) view.getItemAtPosition(i);
            if(selection == 0){
                view.setSelection(selection);
                return;
            }
            if (kv != null && kv.codeValue == selection) {
                view.setSelection(i);
                return;
            }
        }
    }

    @BindingAdapter("enableButton")
    public static void enableButton(Button vg, boolean enable) {
        vg.setEnabled(enable);
    }

    @BindingAdapter("enableControls")
    public static void enableControls(ViewGroup vg, boolean enable) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child.isEnabled() != enable)
                child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                enableControls((ViewGroup) child, enable);
            }
        }
    }

    public static void colorLayouts(Context context, ViewGroup vg) {
        Resources res = context.getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.border_shape, null);

        int children = vg.getChildCount();
        for (int i = 0; i < children; i++) {
            View v = vg.getChildAt(i);


            if (!(v instanceof Button) && !(v instanceof EditText)) {

                if (v instanceof TextView) {
                    ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);

                    if (!(v instanceof RadioButton) && ((TextView) v).getMaxEms() <= 0 && !((TextView) v).getTypeface().isBold()) {
                        v.setBackground(drawable);
                    } else if (((TextView) v).getTypeface().isBold()) {
                        ((TextView) v).setMinHeight(50);
                        ((TextView) v).setTextColor(Color.WHITE);
                        v.setBackgroundColor(Color.DKGRAY);
                        v.setPadding(5,5,5,5);

                    } else {
                        ((TextView) v).setTextColor(Color.MAGENTA);
                        ((TextView) v).setMinHeight(50);
                    }
                }

            }

            if (v instanceof RadioButton) {
                ((RadioButton) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                ((RadioButton) v).setTextColor(Color.MAGENTA);
                ((RadioButton) v).setMinHeight(50);

            }

            if (v instanceof EditText) {
                ((EditText) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                ((EditText) v).setTextColor(Color.MAGENTA);
                ((EditText) v).setMinHeight(50);

            }

            if (v instanceof ViewGroup) {

                colorLayouts(context, ((ViewGroup) v));

            }
        }
    }


}
