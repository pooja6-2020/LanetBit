package in.lanetbit.utils.customView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.lanetbit.LanetBitApplication;

public class TextViewUbuntuRegular extends AppCompatTextView {
    public TextViewUbuntuRegular(Context context) {
        super(context);
        setTypeface(LanetBitApplication.getTextUbuntuRegular());
        setFocusable(true);
    }

    public TextViewUbuntuRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(LanetBitApplication.getTextUbuntuRegular());
        setFocusable(true);
    }

    public TextViewUbuntuRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(LanetBitApplication.getTextUbuntuRegular());
        setFocusable(true);
    }
}
