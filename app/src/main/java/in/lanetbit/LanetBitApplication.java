package in.lanetbit;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Abhimanyu Kumar on 20,March,2020
 **/
public class LanetBitApplication extends Application {

    static Typeface textUbuntuRegular;

    static Typeface textUbuntuLight;

    static Typeface textUbuntuBold;

    static Typeface textUbuntuMedium;

    static Typeface editTextUbuntuLight;
    private static Context context;
    public static String PREF_NAME = "LanetBit";
    private static LanetBitApplication mInstance;

    public static Typeface getTextUbuntuLight() {
        return textUbuntuLight;
    }

    public static Typeface getTextUbuntuMedium() {
        return textUbuntuMedium;
    }

    public static Typeface getTextUbuntuRegular() {
        return textUbuntuRegular;
    }

    public static Typeface getTextUbuntuBold() {
        return textUbuntuBold;
    }

    public static Typeface getEditTextUbuntuLight() {
        return editTextUbuntuLight;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();


        editTextUbuntuLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Ubuntu-LI.ttf");
        textUbuntuLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Ubuntu-LI.ttf");
        textUbuntuMedium = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Ubuntu-M.ttf");
        textUbuntuRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Ubuntu-R.ttf");
        textUbuntuBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Ubuntu-B.ttf");
    }
    public static Context getContext() {
        return context;
    }
}
