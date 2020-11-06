package in.lanetbit.utils.customView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.lanetbit.LanetBitApplication;

public class TextViewUbuntuBold extends AppCompatTextView {
    public TextViewUbuntuBold(Context context) {
        super(context);
        setTypeface(LanetBitApplication.getTextUbuntuBold());
        setFocusable(true);
    }

    public TextViewUbuntuBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(LanetBitApplication.getTextUbuntuBold());
        setFocusable(true);
    }

    public TextViewUbuntuBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(LanetBitApplication.getTextUbuntuBold());
        setFocusable(true);
    }
}
