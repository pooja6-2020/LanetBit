package in.lanetbit.utils.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import in.lanetbit.LanetBitApplication;

public class EditTextUbuntuLight extends AppCompatEditText {
    public EditTextUbuntuLight(Context context) {
        super(context);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }

    public EditTextUbuntuLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }

    public EditTextUbuntuLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }
}
