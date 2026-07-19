package com.app.chillout_delivery.base;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.app.chillout_delivery.ChillOutApplication;
import com.app.chillout_delivery.dialogfragment.LoadingDialogFragment;

public class BaseActivity extends AppCompatActivity {

    public LoadingDialogFragment loadingDialogFragment;
//    public ChooseImageDialogFragment chooseImageDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialogFragment = new LoadingDialogFragment();
//        chooseImageDialogFragment = new ChooseImageDialogFragment();
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
        // java.io.FileNotFoundException: Failed to validate: content://media/external/images/media/1000021058 -- BY SAMU(1.2.5)
        // try to retrieve the image from the media store first
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
        // java.io.FileNotFoundException: Failed to validate: content://media/external/images/media/1000021058 -- BY SAMU(1.2.5)
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
}
