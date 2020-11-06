package in.lanetbit.utils.customView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.lanetbit.LanetBitApplication;

/**
 * Created by Abhimanyu Kumar on 20,March,2020
 **/
public class TextViewUbuntuLight extends AppCompatTextView {
    public TextViewUbuntuLight(Context context) {
        super(context);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }

    public TextViewUbuntuLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }

    public TextViewUbuntuLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(LanetBitApplication.getTextUbuntuLight());
        setFocusable(true);
    }
}
