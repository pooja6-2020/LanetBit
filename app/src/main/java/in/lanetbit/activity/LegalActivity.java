package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.UserInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;

public class LegalActivity extends AppCompatActivity {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    UserInterface apiService;

    String TAG = LegalActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = LegalActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(UserInterface.class);
        initElements();
    }

    private void initElements() {
    }
}
