package in.lanetbit.utils.customView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.lanetbit.LanetBitApplication;

public class TextViewUbuntuMedium extends AppCompatTextView {
    public TextViewUbuntuMedium(Context context) {
        super(context);
        setTypeface(LanetBitApplication.getTextUbuntuMedium());
        setFocusable(true);
    }

    public TextViewUbuntuMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(LanetBitApplication.getTextUbuntuMedium());
        setFocusable(true);
    }

    public TextViewUbuntuMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(LanetBitApplication.getTextUbuntuMedium());
        setFocusable(true);
    }
}
