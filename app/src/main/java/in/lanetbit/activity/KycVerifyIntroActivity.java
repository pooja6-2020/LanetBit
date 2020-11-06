package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;

public class KycVerifyIntroActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = KycVerifyIntroActivity.class.getName();
    private TextView textViewSkip,textViewNoVerifyId,textViewKycVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_verify_intro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = KycVerifyIntroActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);

        initElements();



    }

    private void initElements() {
        textViewKycVerify = findViewById(R.id.tv_kyc_verify);
        textViewSkip = findViewById(R.id.tv_no_verify);
        textViewNoVerifyId = findViewById(R.id.tv_no_id_verify);
        textViewSkip.setOnClickListener(this);
        textViewNoVerifyId.setOnClickListener(this);
        textViewKycVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no_verify:
                Intent intent = new Intent(mContext,DashboardActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.tv_no_id_verify:
                Intent i = new Intent(mContext,DashboardActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.tv_kyc_verify:
                Intent i1 = new Intent(mContext, DocumentVerifyInfoActivity.class);
                startActivity(i1);
                finish();
                break;
        }
    }
}
