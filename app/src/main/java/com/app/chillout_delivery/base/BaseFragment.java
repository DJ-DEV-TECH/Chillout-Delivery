package com.app.chillout_delivery.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.app.chillout_delivery.ChillOutApplication;
import com.app.chillout_delivery.bottomsheetdialog.OrderDetailsListFragment;
import com.app.chillout_delivery.retrofit.ApiClient;
import com.app.chillout_delivery.retrofit.ApiService;
import com.app.chillout_delivery.utils.PrefsHelper;

public class BaseFragment extends Fragment {

    public static final String STATUS_EVENT = "com.app.chillout_admin.STATUS_EVENT";
    public PrefsHelper prefsHelper;
    public String name = "";
    public String mobile = "";
    public String email = "";
    public String authToken = "";
    public int status = 0;
    public ApiService apiService;
    public OrderDetailsListFragment orderDetailsListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsHelper = new PrefsHelper(getActivity());
        apiService = ApiClient.getLoginApiClient().create(ApiService.class);
        name = ChillOutApplication.getInstance().getUserName();
        mobile = ChillOutApplication.getInstance().getMobile();
        email = ChillOutApplication.getInstance().getEmail();
        authToken = ChillOutApplication.getInstance().getAuthToken();
        status = ChillOutApplication.getInstance().getStatus();
        orderDetailsListFragment = new OrderDetailsListFragment();
    }
}
