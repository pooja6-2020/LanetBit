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

public class FaQActivity extends AppCompatActivity {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    UserInterface apiService;

    String TAG = FaQActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fa_q);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = FaQActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(UserInterface.class);
        initElements();
    }

    private void initElements() {

    }
}
