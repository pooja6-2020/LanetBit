package in.lanetbit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import in.lanetbit.LanetBitApplication;

public class PrefStorage {
  // Shared preferences file name
  private static final String PREF_NAME = LanetBitApplication.PREF_NAME;
  // LogCat tag
  private static String TAG = PrefStorage.class.getSimpleName();
  SharedPreferences pref;
  SharedPreferences.Editor editor;
  Context _context;
  // Shared pref mode
  int PRIVATE_MODE = 0;

  public PrefStorage(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public void setValueString(String key, String str) {
    editor.putString(key, str);
    editor.commit();
    Log.d(TAG, key + ":" + str);
  }

  public String getValueString(String key) {
    return pref.getString(key, "");
  }

  public void setValueBoolean(String key, Boolean str) {
    editor.putBoolean(key, str);
    editor.commit();
    Log.d(TAG, key + ":" + str);
  }

  public void clearStorageData() {

    pref.edit().clear().commit();

  }

  public Boolean getValueBoolean(String key) {
    Boolean bdata = pref.getBoolean(key, false);
    Log.d(TAG, key + ":" + bdata);
    return bdata;
  }

  public String getJwtToken() {
    return getValueString("JWT_TOKEN");
  }

  public void setJwtToken(String stringJwtToken) {
    editor.putString("JWT_TOKEN", stringJwtToken);
    editor.commit();
  }

//    public void setUserDetails(JwtTokenDetail jwtTokenDetail) {
//        editor.putString("USER_ID", jwtTokenDetail.userId);
//        editor.putString("USER_TYPE", jwtTokenDetail.userType);
//        editor.putString("USER_NAME", jwtTokenDetail.userName);
//        editor.putString("USER_UNREAD_NOTIFY", jwtTokenDetail.userUnreadNotification);
////        editor.putString("USER_NAME",jwtTokenDetail.userName);
////        editor.putString("USER_EMAIL",jwtTokenDetail.userEmail);
////        editor.putString("USER_MOBILE",jwtTokenDetail.userMobile);
//        editor.commit();
//    }

}
