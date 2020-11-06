package in.lanetbit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import id.zelory.compressor.Compressor;
import in.lanetbit.R;
import in.lanetbit.apis.ApiClient;
import in.lanetbit.apis.AuthInterface;
import in.lanetbit.apis.UserInterface;
import in.lanetbit.utils.AppConstants;
import in.lanetbit.utils.PrefStorage;
import in.lanetbit.utils.TLHelper;
import in.lanetbit.utils.Utilities;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Context mContext;
    PrefStorage prefStorage;
    TLHelper hlp;
    UserInterface apiService;

    String TAG = EditProfileActivity.class.getName();

    private ImageView imageViewProfile, imageViewAddProfile;
    private TextView textViewFullName, textViewEmail, textViewCountry, textViewMobileNo, textViewSave;
    private static int REQUEST_GET_SINGLE_FILE = 10;
    private  Uri selectedImageUri;
    private Bitmap mResultsBitmap;
    private EditText editTextFullName;
    private String stringAuthorization,stringFullName,stringProfileImage,secondIconPath,userChoosenTask;
    private JSONObject jsonObjectUserPayload;
    private static int RESULT_LOAD_IMAGE = 10;
    Cursor cursor;
    private int PICK_IMAGE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = EditProfileActivity.this;
        prefStorage = new PrefStorage(mContext);
        hlp = new TLHelper(mContext);
        apiService = ApiClient.getClient().create(UserInterface.class);

        initElement();

    }

    private void initElement() {
        imageViewAddProfile = findViewById(R.id.img_btn_add_photo);
        imageViewProfile = findViewById(R.id.img_profile_pic);
        editTextFullName = findViewById(R.id.ed_full_name);
        textViewEmail = findViewById(R.id.tv_email);
        textViewCountry = findViewById(R.id.tv_country);
        textViewMobileNo = findViewById(R.id.tv_phone);
        textViewSave = findViewById(R.id.tv_save);
        textViewSave.setOnClickListener(this);
        imageViewAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromGallery(PICK_IMAGE1);

            }
        });

        findViewById(R.id.img_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        editTextFullName.setText(prefStorage.getValueString(AppConstants.UserPrefKey.USER_FULL_NAME));
        textViewEmail.setText(prefStorage.getValueString(AppConstants.UserPrefKey.USER_EMAIL));
        textViewCountry.setText("N/A");
        textViewMobileNo.setText(prefStorage.getValueString(AppConstants.UserPrefKey.USER_MOBILE));

        editTextFullName.setOnClickListener(this);
        if (prefStorage.getValueString(AppConstants.UserPrefKey.USER_PROFILE_IMAGE).isEmpty()) {
            Glide.with(mContext)
                    .load(R.drawable.ic_user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);

        } else
            Glide.with(mContext)
                    .load(AppConstants.USER_IMAGE_BASE_URL+prefStorage.getValueString(AppConstants.UserPrefKey.USER_PROFILE_IMAGE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);


    }
    private void loadImageFromGallery(final int RequestCode) {
        final CharSequence[] items = {Html.fromHtml("<font color='#818181'>Choose from Gallery</font>"),
                Html.fromHtml("<font color='#818181'>Cancel</font>")};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        builder.setTitle("Upload Image !");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utilities.checkPermission(mContext);
                if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
                        galleryIntent(RequestCode);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_save:
                    stringFullName= editTextFullName.getText().toString();
                    updateUserDetails(stringFullName);
                break;
            case R.id.ed_full_name:
                editTextFullName.setCursorVisible(true);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            final Uri selectedImage = data.getData();

            if (requestCode == PICK_IMAGE1) {
                imageViewProfile.setImageURI(selectedImage);
                cursor = mContext.getContentResolver().query(selectedImage, null, null, null, null);
                cursor.moveToFirst();
                String image_id = cursor.getString(0);
                image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
                cursor.close();
                cursor = mContext.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
                if (cursor.moveToFirst()) {
                    stringProfileImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                    imageViewRemoveButton1.setVisibility(View.VISIBLE);
//                    imgBtnAddPhoto1.setVisibility(View.GONE);
//                    imageViewRemoveButton1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            File file = new File(String.valueOf(selectedImage));
//                            file.delete();
//                            imgWatchPic1.setImageURI(null);
//                            imgBtnAddPhoto1.setVisibility(View.VISIBLE);
//                            imageViewRemoveButton1.setVisibility(View.GONE);
//                        }
//                    });
                } else {
                    hlp.showToast("Please choose another images");
                }
                cursor.close();
            }
        }
    }

    private void dialogImageUpload() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Are you sure, You wanted to upload this image");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        updateUserDetails(stringFullName);
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

    public String getSecondIconPath() {
        return secondIconPath;
    }

    public void setSecondIconPath(String secondIconPath) {
        this.secondIconPath = secondIconPath;
    }
    private void updateUserDetails(String stringFullName) {

        stringAuthorization = "Bearer" + " " + prefStorage.getJwtToken();
        hlp.showLoader("Please wait ...");
        try {

            jsonObjectUserPayload = new JSONObject();
            jsonObjectUserPayload.put("fullName", stringFullName);
            Log.e("json_user_detail?", String.valueOf(jsonObjectUserPayload));

        }catch (JSONException e){
            e.printStackTrace();
        }
        MultipartBody.Part docImage = null;

        if (stringProfileImage != null) {
            try {
                File file1 = new File(stringProfileImage);
                File fileCompressed = new Compressor(EditProfileActivity.this).compressToFile(file1);
                if (fileCompressed != null) {
                    file1 = fileCompressed;
                } else {
                    file1 = new File(stringProfileImage);
                }
                RequestBody reqFile1 = RequestBody.create(MediaType.parse("text/plain"), file1);
                docImage = MultipartBody.Part.createFormData("files", file1.getName(), reqFile1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        LinkedHashMap<String, JSONObject> paramObject = new LinkedHashMap<>();
        paramObject.put("payload",jsonObjectUserPayload);

        Call<JsonElement> call = apiService.postUpdateUserDetail(stringAuthorization, docImage,paramObject);
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

                    if (responseJson.getString("code").equalsIgnoreCase("201") && responseJson.getString("type").equalsIgnoreCase("success")) {
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utilities.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hlp.showToast("Permission granted successfully, Now you can upload your images.");
                } else {
                    hlp.showToast(getString(R.string.allow_permission));
                }
                break;
        }
    }
}
