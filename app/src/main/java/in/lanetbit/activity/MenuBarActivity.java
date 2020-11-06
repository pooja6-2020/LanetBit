package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;

public class MenuBarActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = DashboardActivity.class.getName();
    private ImageView imageViewClose, imageViewProfile;
    private TextView textViewFullName, textViewLastLogin, textViewCancel, textViewOk;
    private LinearLayout layoutCryptoToCrypto,layoutWallet, layoutLogOut, layoutEditProfile, layoutSettings, layoutBankAccount, layoutDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = MenuBarActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);

        initElement();

    }

    private void initElement() {
        imageViewProfile = findViewById(R.id.iv_profile);
        imageViewClose = findViewById(R.id.iv_close);
        textViewFullName = findViewById(R.id.tv_full_name);
        textViewLastLogin = findViewById(R.id.tv_last_login);
        layoutEditProfile = findViewById(R.id.ll_edit_profile);
        layoutLogOut = findViewById(R.id.ll_log_out);
        layoutWallet = findViewById(R.id.ll_wallet);
        layoutSettings = findViewById(R.id.ll_settings);
        layoutBankAccount = findViewById(R.id.ll_bank_account);
        layoutDashboard = findViewById(R.id.ll_dashboard);
        layoutCryptoToCrypto = findViewById(R.id.ll_crypto);
        layoutCryptoToCrypto.setOnClickListener(this);
        layoutDashboard.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
        layoutLogOut.setOnClickListener(this);
        layoutEditProfile.setOnClickListener(this);
        layoutSettings.setOnClickListener(this);
        layoutBankAccount.setOnClickListener(this);
        layoutWallet.setOnClickListener(this);

        textViewFullName.setText(prefStorage.getValueString(AppConstants.UserPrefKey.USER_FULL_NAME));
        textViewLastLogin.setText(prefStorage.getValueString(AppConstants.UserPrefKey.USER_LAST_LOGIN));

        if (prefStorage.getValueString(AppConstants.UserPrefKey.USER_PROFILE_IMAGE).isEmpty()) {
            Glide.with(mContext)
                    .load(R.drawable.ic_user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);

        } else
            Glide.with(mContext)
                    .load(AppConstants.USER_IMAGE_BASE_URL + prefStorage.getValueString(AppConstants.UserPrefKey.USER_PROFILE_IMAGE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_profile:
                Intent intent = new Intent(mContext, EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_close:

                super.onBackPressed();

                break;
            case R.id.ll_settings:
                Intent intent1 = new Intent(mContext, SettingsActivity.class);
                startActivity(intent1);

                break;
            case R.id.ll_bank_account:
                Intent intents = new Intent(mContext, BankAccountActivity.class);
                startActivity(intents);

                break;
            case R.id.ll_wallet:
                Intent inten = new Intent(mContext, OrderHistoryActivity.class);
                startActivity(inten);

                break;
            case R.id.ll_crypto:
                Intent inte = new Intent(mContext, CryptoToCryptoActivity.class);
                startActivity(inte);

                break;
            case R.id.ll_dashboard:
                Intent intenq = new Intent(mContext, DashboardActivity.class);
                startActivity(intenq);

                break;
            case R.id.ll_log_out:
                actionLogOut();
                break;
        }
    }

    private void actionLogOut() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_action_log_out);
        textViewCancel = dialog.findViewById(R.id.tv_cancel);
        textViewOk = dialog.findViewById(R.id.tv_ok);
        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefStorage.clearStorageData();
                startActivity(new Intent(mContext, AuthActivity.class));

                finish();
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    }
}
