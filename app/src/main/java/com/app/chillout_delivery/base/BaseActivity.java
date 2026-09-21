package com.app.chillout_delivery.base;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.app.chillout_delivery.ChillOutApplication;
import com.app.chillout_delivery.R;
import com.app.chillout_delivery.dialogfragment.LoadingDialogFragment;
import com.app.chillout_delivery.retrofit.ApiClient;
import com.app.chillout_delivery.retrofit.ApiService;
import com.app.chillout_delivery.utils.PrefsHelper;
import com.app.chillout_delivery.utils.SocketManager;

public class BaseActivity extends AppCompatActivity {

    public static final String STATUS_EVENT = "com.app.chillout_admin.STATUS_EVENT";
    public LoadingDialogFragment loadingDialogFragment;
//    public ChooseImageDialogFragment chooseImageDialogFragment;
    public SocketManager socketManager;
    public PrefsHelper prefsHelper;
    public ApiService apiService;
    public String name = "";
    public String mobile = "";
    public String email = "";
    public String authToken = "";
    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsHelper = new PrefsHelper(this);
        apiService = ApiClient.getLoginApiClient().create(ApiService.class);
        loadingDialogFragment = new LoadingDialogFragment();
//        chooseImageDialogFragment = new ChooseImageDialogFragment();
        name = ChillOutApplication.getInstance().getUserName();
        mobile = ChillOutApplication.getInstance().getMobile();
        email = ChillOutApplication.getInstance().getEmail();
        authToken = ChillOutApplication.getInstance().getAuthToken();
        status = ChillOutApplication.getInstance().getStatus();
    }

    public void showLoading() {
        loadingDialogFragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG);
    }

    public void closeLoading() {
        if (loadingDialogFragment.isVisible())
            loadingDialogFragment.dismiss();
    }

    public static String getPath(Context context, Uri uri) {
        // just some safety built in
        Cursor cursor = null;
        if (uri == null) {
            return null;
        }
        // this will only work for images selected from gallery
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        }catch (Exception e){
//            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // this is our fallback here
        if(uri.isAbsolute())
            return uri.getPath();
        else
            return null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public ChillOutApplication application() {
        return (ChillOutApplication) getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void playSound(Context context) {
        try {
            if (application().mediaPlayer == null) {
                application().mediaPlayer = MediaPlayer.create(context, R.raw.new_order);
            }

            if (application().mediaPlayer != null) {
                application().mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
