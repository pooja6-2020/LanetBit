package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;


public class DocumentVerifyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = DocumentVerifyInfoActivity.class.getName();

    private LinearLayout layoutPassport, layoutDriverLicense, layoutIdentityCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_verify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = DocumentVerifyInfoActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
        initElements();


    }

    private void initElements() {
        layoutPassport = findViewById(R.id.ll_passport);
        layoutDriverLicense = findViewById(R.id.ll_driver_license);
        layoutIdentityCard = findViewById(R.id.ll_identity);

        layoutPassport.setOnClickListener(this);
        layoutDriverLicense.setOnClickListener(this);
        layoutIdentityCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_passport:
                Intent i1 = new Intent(mContext, DocumentVerifyActivity.class);
                i1.putExtra("KYC_DOC_TYPE", "PASSPORT");

                startActivity(i1);
                break;

            case R.id.ll_driver_license:
                Intent i2 = new Intent(mContext, DocumentVerifyActivity.class);
                i2.putExtra("KYC_DOC_TYPE", "DL");

                startActivity(i2);

                break;

            case R.id.ll_identity:
                Intent i3 = new Intent(mContext, DocumentVerifyActivity.class);
                i3.putExtra("KYC_DOC_TYPE", "IDENTITY");

                startActivity(i3);

                break;
        }

    }
}
