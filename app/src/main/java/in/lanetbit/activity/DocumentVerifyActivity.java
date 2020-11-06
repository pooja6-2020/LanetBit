package in.lanetbit.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import id.zelory.compressor.Compressor;
import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentVerifyActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    AuthInterface apiService;

    String TAG = DocumentVerifyActivity.class.getName();
    private String stringAuthorization,secondIconPath,stringDocType,stringDocTypeImage,stringDocId;
    private LinearLayout layoutSelfieIdentity,layoutUploadProof,layoutPassport, layoutDriverLicense, layoutIdentityCard;
    private static int REQUEST_GET_SINGLE_FILE = 10;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;

    Cursor cursor;
        private  Uri selectedImageUri;
        private EditText editTextDocId,editTextDocIdCapture;
        private ImageView imageView;
        private TextView textViewSubmit,textViewUploaded,textViewCapture;
        private JsonObject jsonObjectKycDocPayload;
        private Bitmap bitmapKycDocCapture;
    MultipartBody.Part docImage;
    RequestBody reqFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_verify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = DocumentVerifyActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(AuthInterface.class);
            Intent intent = getIntent();
        stringDocType = intent.getStringExtra("KYC_DOC_TYPE");

        initiateAllView();

        Log.e("DocType ?",stringDocType);

    }

    private void initiateAllView() {
        textViewSubmit = findViewById(R.id.tv_submit);
        textViewUploaded = findViewById(R.id.tv_uploaded);
        imageView = findViewById(R.id.image);
        layoutSelfieIdentity = findViewById(R.id.ll_selfie_identity);
        layoutUploadProof = findViewById(R.id.ll_upload_proof);
        layoutPassport = findViewById(R.id.ll_passport);
        layoutDriverLicense = findViewById(R.id.ll_driver_license);
        layoutIdentityCard = findViewById(R.id.ll_identity);
        editTextDocId = findViewById(R.id.ed_doc_id);
        editTextDocIdCapture = findViewById(R.id.ed_doc_id_capture);
        if (stringDocType.matches("PASSPORT")){
            layoutPassport.setVisibility(View.VISIBLE);
            layoutDriverLicense.setVisibility(View.GONE);
            layoutIdentityCard.setVisibility(View.GONE);

        }else if (stringDocType.matches("DRIVER_LICENSE")){
            layoutPassport.setVisibility(View.GONE);
            layoutDriverLicense.setVisibility(View.VISIBLE);
            layoutIdentityCard.setVisibility(View.GONE);
        }else if (stringDocType.matches("IDENTITY")){
            layoutPassport.setVisibility(View.GONE);
            layoutDriverLicense.setVisibility(View.GONE);
            layoutIdentityCard.setVisibility(View.VISIBLE);
        }
        layoutSelfieIdentity.setOnClickListener(this);
        layoutUploadProof.setOnClickListener(this);
        textViewSubmit.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_selfie_identity:

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;
            case R.id.ll_upload_proof:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GET_SINGLE_FILE);

                break;
            case R.id.tv_submit:
                if (selectedImageUri!=null) {

                    if (editTextDocId.getText().toString().isEmpty()){
                        hlp.showToast("Please enter your doc Id ! ");
                    }else {
                        dialogImageUpload();

                    }

                }else if(bitmapKycDocCapture!=null){
                    if (editTextDocIdCapture.getText().toString().isEmpty()){
                        hlp.showToast("Please enter your doc Id ! ");
                    }else {
                        dialogImageUpload();

                    }
                } else hlp.showToast("Upload your proof identity!");

                break;

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                hlp.showToast( "camera permission granted");
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                hlp.showToast( "camera permission denied");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                     selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    // Set the image in ImageView
                    if (selectedImageUri!=null){
                        textViewUploaded.setVisibility(View.VISIBLE);
                    }
                    Log.e("image_doc?", String.valueOf(selectedImageUri));

                }
            }
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
            {
                bitmapKycDocCapture = (Bitmap) data.getExtras().get("data");
            //    imageView.setImageBitmap(photo);
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void dialogImageUpload() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Are you sure, You wanted to upload this image");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    if (selectedImageUri!=null){
                        stringDocId = editTextDocId.getText().toString();

                        updateUserDetails(stringDocId);

                    }else if (bitmapKycDocCapture!=null){
                        stringDocId = editTextDocIdCapture.getText().toString();
                        updateUserDetails(stringDocId);

                    }

                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateUserDetails(String stringDocId) {

        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        hlp.showLoader("Please wait ...");
        try {

             jsonObjectKycDocPayload = new JsonObject();
            jsonObjectKycDocPayload.addProperty("docType",stringDocType);
            jsonObjectKycDocPayload.addProperty("docId",stringDocId);
            Log.e("json_kyc_doc_detail?", String.valueOf(jsonObjectKycDocPayload));

        }catch (Exception e){
            e.printStackTrace();
        }
        if (selectedImageUri!=null){
            File file = new File(String.valueOf(selectedImageUri));
            File fileCompressed = null;
            try {
                fileCompressed = new Compressor(mContext).compressToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileCompressed != null) {
                file = fileCompressed;
            } else {
                file = new File(String.valueOf(selectedImageUri));
            }
            reqFile = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedImageUri));

            docImage = MultipartBody.Part.createFormData("files", String.valueOf(selectedImageUri));

        }else if (bitmapKycDocCapture!=null){
            File file = new File(String.valueOf(bitmapKycDocCapture));
            File fileCompressed = null;
            try {
                fileCompressed = new Compressor(mContext).compressToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileCompressed != null) {
                file = fileCompressed;
            } else {
                file = new File(String.valueOf(bitmapKycDocCapture));
            }
            reqFile = RequestBody.create(MediaType.parse("text/plain"),file);

            docImage = MultipartBody.Part.createFormData("files", file.getName(),reqFile);

        }

        LinkedHashMap<String, JsonObject> paramObject = new LinkedHashMap<>();
            paramObject.put("payload", jsonObjectKycDocPayload);

        Call<JsonElement> call = apiService.postKycDoc(stringAuthorization, docImage,paramObject);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                hlp.hideLoader();
                JsonElement jsonElement = response.body();
                assert jsonElement != null;
                String body = jsonElement.toString();
                Log.e(TAG, "Api Response : " + body);
                try {

                    JSONObject responseJson = new JSONObject(body);

                    if (responseJson.getString("code").equalsIgnoreCase("200") && responseJson.getString("type").equalsIgnoreCase("success")) {

                        Intent intent = new Intent(mContext,DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        hlp.showToast(responseJson.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                hlp.hideLoader();
                hlp.noConnection();

                t.printStackTrace();

            }
        });

    }

}
