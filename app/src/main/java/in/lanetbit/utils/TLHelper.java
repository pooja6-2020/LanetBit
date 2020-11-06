package in.lanetbit.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import in.lanetbit.R;
import in.lanetbit.fragment.SignInFragment;

public class TLHelper {

    private ProgressDialog pDialog;
    private Context mContext;

    public TLHelper(Context context) {
        this.mContext = context;
        pDialog = new ProgressDialog(context);
    }

    public void callSignOut(){
        PrefStorage prefStorage = new PrefStorage(mContext);
        prefStorage.clearStorageData();
        mContext.startActivity(new Intent(mContext, SignInFragment.class));
    }

    public void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.confirmDialog);
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(msg);
        // On pressing Settings button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(this.mContext, msg, Toast.LENGTH_SHORT).show();
    }


    public float dpFromPx(final float px) {
        return px / mContext.getResources().getDisplayMetrics().density;
    }

    public float pxFromDp(final float dp) {
        return dp * mContext.getResources().getDisplayMetrics().density;
    }

    public void showLoader(String str) {
        viewLoader(str, false);
    }

    public void showLoader(String str, Boolean cancelleble) {
        viewLoader(str, cancelleble);
    }

    private void viewLoader(String str, Boolean cancelleble) {
        try {
            pDialog.setCancelable(cancelleble);
            pDialog.setMessage(str);
            if (pDialog.isShowing())
                pDialog.dismiss();
            pDialog.show();
        } catch (Exception e) {

        }
    }

    public double setRound(double d, int c) {
        int temp = (int) (d * Math.pow(10, c));
        return ((double) temp) / Math.pow(10, c);
    }


    private String removeZeros(String s) {
        s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
        return s;

    }

    public String makeRound(String val, String prec) {
        String str = String.format("%." + prec + "f", Double.parseDouble(val));
        return str;
    }

    public void hideLoader() {
        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {

        }
    }

    public String getDeviceID() {
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public void noConnection() {
        showToast("No Internet Connection");
    }

    public boolean isSimSupport() {
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
            return false;
        } else {
            return true;
        }
    }

    public int getRandomInteger(int aStart, int aEnd) {
        Random aRandom = new Random();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }

    public void startSMSReceive() {
        PrefStorage tls = new PrefStorage(this.mContext);
        tls.setValueBoolean("SMS_RECEIVE", true);
    }

    public void stopSMSReceive() {
        PrefStorage tls = new PrefStorage(this.mContext);
        tls.setValueBoolean("SMS_RECEIVE", false);
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String ImageToString(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final View mProgress, final View mForm, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public String ByteToString(byte[] bdata) {
        return Base64.encodeToString(bdata, 0);
    }


    public void openAppSetting() {
        Context context = mContext;
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public String getShortName(String name) {
        String s = "";
        String[] myName = name.split(" ");
        for (int i = 0; i < myName.length; i++) {
            s += myName[i].toUpperCase().charAt(0);
            if (i == 1)
                break;
        }
        return s;
    }

    public String label(String key) {
        try {
            int id = mContext.getResources().getIdentifier(key, "string", mContext.getPackageName());
            return mContext.getResources().getString(id);
        } catch (Exception e) {

        }
        return "";
    }

    public String label(int id) {
        try {
            return mContext.getResources().getString(id);
        } catch (Exception e) {

        }
        return "";
    }

    public String captializeAllFirstLetter(String name) {
        name = name.toLowerCase();
        char[] array = name.toCharArray();
        array[0] = Character.toUpperCase(array[0]);

        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }
}
