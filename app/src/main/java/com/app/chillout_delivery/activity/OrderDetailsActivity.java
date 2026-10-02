package com.app.chillout_delivery.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.app.chillout_delivery.utils.Utils.getOrderId;
import static com.app.chillout_delivery.utils.Utils.showPermissionDialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.chillout_delivery.R;
import com.app.chillout_delivery.adapter.SummaryAdapter;
import com.app.chillout_delivery.base.BaseActivity;
import com.app.chillout_delivery.databinding.ActivityOrderDetailsBinding;
import com.app.chillout_delivery.model.EventModel;
import com.app.chillout_delivery.model.OrderModelData;
import com.app.chillout_delivery.utils.EventManager;
import com.app.chillout_delivery.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.permissionx.guolindev.PermissionX;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity implements OnMapReadyCallback {

    private ActivityOrderDetailsBinding binding;
    private GoogleMap mMap;
    Marker riderMarker;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private SummaryAdapter adapter;
    private LatLng customer;
    LatLng restaurant = new LatLng(11.438407, 77.701524); // Chennai
    String orderId = "";
    OrderModelData orderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOrderEvent();
        orderId = getIntent().getStringExtra("orderId");
//        orderModel = new Gson().fromJson(orderData, OrderModel.class);
        getOrderDetails(orderId);

        getLocationPermission();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            getOrderDetails(orderId);
        });

        binding.backImg.setOnClickListener(v -> {
            finish();
        });

        binding.orderStatusBtn.setOnClickListener(v -> {
            String type = binding.orderStatusBtn.getText().toString();
            if (type.contains("Picked")) {
                updateOrderPicked(""+getOrderId(orderId));
            } else if (type.contains("Delivered")) {
                updateOrderDelivered(""+getOrderId(orderId));
            }
        });

        binding.naviBtn.setOnClickListener(v -> {
            simpleNavi();
        });
    }

    private void setupRecycler(OrderModelData orderModel) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.summaryRecycler.setLayoutManager(layoutManager);
        adapter = new SummaryAdapter(orderModel.getData().getItems(), this);
        binding.summaryRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadMap() {
        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, mapFragment)
                .commitNow();

        mapFragment.getMapAsync(this);
    }

    private void mainDataLoad(OrderModelData orderModel) {
        new Handler(Looper.getMainLooper()).postDelayed(this::loadMap, 300);
        binding.shimmerCard.setVisibility(View.VISIBLE);
        binding.mapShimmerLayout.startShimmer();

        if (orderModel.getData() != null) {
            binding.orderIdTxt.setText("#"+orderModel.getData().getOrderId());
            binding.totalTxt.setText("₹"+orderModel.getData().getTotalAmount());
            binding.orderStatusTxt.setText(orderModel.getData().getOrderStatus());
            switch (orderModel.getData().getOrderStatus()) {
                case "PENDING":
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
                    binding.submitConstraint.setVisibility(GONE);
                    break;
                case "ACCEPTED":
                case "ASSIGNED":
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    binding.submitConstraint.setVisibility(VISIBLE);
                    binding.orderStatusBtn.setText("Marked as Picked");
                    break;
                case "PICKED":
                case "OUT FOR DELIVERY":
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    binding.submitConstraint.setVisibility(VISIBLE);
                    binding.orderStatusBtn.setText("Marked as Delivered");
                    break;
                case "COMPLETED":
                case "SUCCESS":
                case "DELIVERED":
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
                    binding.submitConstraint.setVisibility(GONE);
                    break;
                default:
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_1)));
                    binding.submitConstraint.setVisibility(GONE);
                    break;
            }

            if (orderModel.getData().getUserDetails() != null) {
                binding.customerNameTxt.setText(orderModel.getData().getUserDetails().getName());
                binding.customerMbleTxt.setText(orderModel.getData().getUserDetails().getMobile());
                if (orderModel.getData().getUserDetails().getAddress() != null) {
                    customer = new LatLng(orderModel.getData().getUserDetails().getAddress().getLat(), orderModel.getData().getUserDetails().getAddress().getLng());
//                    binding.deliveryNameTxt.setText(orderModel.getData().getUserDetails().getAddress().getLabel());
                    binding.deliveryAddressTxt.setText(orderModel.getData().getUserDetails().getAddress().getFullAddress());
                }
            }
            if (orderModel.getData().getDeliveryBoyDetails() != null) {
                binding.deliveryBoyInfoCard.setVisibility(VISIBLE);
                binding.deliveryBoyNameTxt.setText(orderModel.getData().getDeliveryBoyDetails().getName());
                binding.deliveryBoyMbleTxt.setText(orderModel.getData().getDeliveryBoyDetails().getMobile());
            } else
                binding.deliveryBoyInfoCard.setVisibility(GONE);
            setupRecycler(orderModel);
        }
    }

    private void simpleNavi() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + customer.latitude + "," + customer.longitude+"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // force Google Maps
        closeLoading();

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void addressNavi() {
        String address = "JKK Nattraja Nagar\n" +
                "B.Komarapalayam, Tamil Nadu 638183";

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        startActivity(mapIntent);
    }

    private void routePath(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> parseRoute(response),
                error -> Log.e("JK_MAP", error.toString())
        );
        queue.add(request);
    }

    private void parseRoute(JSONObject response) {
        try {
            Log.d("JK_MAP", "response: " + response.toString());
            JSONArray routes = response.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);

            JSONObject polyline =
                    route.getJSONObject("overview_polyline");

            String encodedPolyline = polyline.getString("points");

            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);

            String distance =
                    leg.getJSONObject("distance").getString("text");

            String duration =
                    leg.getJSONObject("duration").getString("text");

            drawRoute(encodedPolyline);

            binding.durationTxt.setText("Duration : "+duration);
            binding.distanceTxt.setText("Distance : "+distance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawRoute(String encodedPolyline) {
        List<LatLng> points = PolyUtil.decode(encodedPolyline);

        PolylineOptions polylineOptions =
                new PolylineOptions()
                        .addAll(points)
                        .width(12f)
                        .color(Color.RED)
                        .geodesic(true);

        mMap.addPolyline(polylineOptions);

        // 🔥 AUTO ZOOM TO ROUTE (THIS IS THE FIX)
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point); // 👈 IMPORTANT
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        /*// animate after map layout ready
        mMap.setOnMapLoadedCallback(() -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        });*/
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + restaurant.latitude + "," + restaurant.longitude +
                        "&destination=" + customer.latitude + "," + customer.longitude +
                        "&key=AIzaSyBRcTL0kqdN1xpQzW2Kq4zr5nm6veWAek8";

        Bitmap circleBitmap = createCustomMarker(this, R.drawable.chillout_logo);
        Bitmap circleBitmap1 = createCustomMarkerTest(this, R.drawable.profile);

        // End Marker
        mMap.addMarker(new MarkerOptions()
                .position(customer)
                .title("Customer")
                .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap)));

        riderMarker = mMap.addMarker(new MarkerOptions()
                .position(restaurant)
                .title("Rider")
                .anchor(0.5f, 0.5f)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap1)));

        mMap.setOnMapLoadedCallback(() -> {
            binding.shimmerCard.setVisibility(View.GONE);
            binding.mapShimmerLayout.stopShimmer();
            binding.mapCard.setVisibility(View.VISIBLE);
            routePath(url); // call after map fully loaded
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            getCurrentLocationAndAddMarker();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap createCustomMarker(Context context, int imageRes) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.marker_layout, null);
        CircleImageView image = markerView.findViewById(R.id.markerImg);
        image.setImageResource(imageRes);

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return bitmap;
    }

    public Bitmap createCustomMarkerTest(Context context, int imageRes) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.marker_layout, null);
        CircleImageView image = markerView.findViewById(R.id.markerImg);
        image.setImageResource(imageRes);

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return bitmap;
    }

    public void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ✅ Android 10+ (API 29+)
            PermissionX.init(this)
                    .permissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
//                            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please allow location permission", Toast.LENGTH_SHORT).show();
                            showPermissionDialog(this);
                        }
                    });

        } else {
            // ✅ Below Android 10
            PermissionX.init(this)
                    .permissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
//                            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please allow location permission", Toast.LENGTH_SHORT).show();
                            showPermissionDialog(this);
                        }
                    });
        }
    }

    private void getOrderDetails(String orderId) {
        apiService.getOrderDetails(Utils.getAuthToken(authToken), orderId, userId).enqueue(new Callback<OrderModelData>() {
            @Override
            public void onResponse(Call<OrderModelData> call, Response<OrderModelData> response) {
                closeLoading();
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getCode() == 200) {
                    orderModel = response.body();
                    mainDataLoad(orderModel);
                } else {
                    finish();
                    Toast.makeText(OrderDetailsActivity.this,
                            "Order Data Fetch Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModelData> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderPicked(String orderId) {
        apiService.orderPicked(Utils.getAuthToken(authToken), orderId).enqueue(new Callback<OrderModelData>() {
            @Override
            public void onResponse(Call<OrderModelData> call, Response<OrderModelData> response) {
                closeLoading();
                if (response.isSuccessful() && response.body().getCode() == 200) {
                    binding.orderStatusBtn.setText("Mark as Delivered");
                    binding.orderStatusTxt.setText("PICKED");
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(OrderDetailsActivity.this, R.color.orange)));
                } else {
                    Toast.makeText(OrderDetailsActivity.this,
                            ""+response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModelData> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderDelivered(String orderId) {
        apiService.orderDelivered(Utils.getAuthToken(authToken), orderId).enqueue(new Callback<OrderModelData>() {
            @Override
            public void onResponse(Call<OrderModelData> call, Response<OrderModelData> response) {
                closeLoading();
                if (response.isSuccessful() && response.body().getCode() == 200) {
                    binding.orderStatusBtn.setText("Delivered");
                    binding.orderStatusBtn.setEnabled(false);
                    binding.orderStatusBtn.setClickable(false);
                    binding.submitConstraint.setVisibility(GONE);
                    binding.orderStatusTxt.setText("DELIVERED");
                    binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(OrderDetailsActivity.this, R.color.green)));
                    EventManager.getInstance().sendEvent(
                            new EventModel("USER_STATUS", "", "1"));
                } else {
                    Toast.makeText(OrderDetailsActivity.this,
                            ""+response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModelData> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderEvent() {
        EventManager.getInstance().getEvents().observe(this, event -> {
            if (event == null) return;
            switch (event.type) {
                case "ORDER_UPDATE":
                    if (orderId.equalsIgnoreCase(event.orderId)) {
                        OrderModelData orderModelData = new Gson().fromJson(event.data, OrderModelData.class);
                        if (orderModelData.getData() != null) {
                            if (orderId.equalsIgnoreCase(orderModelData.getData().getOrderId())) {
                                binding.orderStatusTxt.setText(orderModelData.getData().getOrderStatus());
                                switch (orderModelData.getData().getOrderStatus()) {
                                    case "PENDING":
                                        binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange)));
                                        binding.submitConstraint.setVisibility(GONE);
                                        break;
                                    case "ACCEPTED":
                                    case "ASSIGNED":
                                        binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                                        binding.submitConstraint.setVisibility(VISIBLE);
                                        binding.orderStatusBtn.setText("Marked as Picked");
                                        break;
                                    case "PICKED":
                                    case "OUT FOR DELIVERY":
                                        binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                                        binding.submitConstraint.setVisibility(VISIBLE);
                                        binding.orderStatusBtn.setText("Marked as Delivered");
                                        break;
                                    case "COMPLETED":
                                    case "SUCCESS":
                                    case "DELIVERED":
                                        binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
                                        binding.submitConstraint.setVisibility(GONE);
                                        break;
                                    default:
                                        binding.orderStatusTxt.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_1)));
                                        binding.submitConstraint.setVisibility(GONE);
                                        break;
                                }

                                if (orderModelData.getData().getDeliveryBoyDetails() != null) {
                                    binding.deliveryBoyInfoCard.setVisibility(VISIBLE);
                                    binding.deliveryBoyNameTxt.setText(orderModelData.getData().getDeliveryBoyDetails().getName());
                                    binding.deliveryBoyMbleTxt.setText(orderModelData.getData().getDeliveryBoyDetails().getMobile());
                                } else {
                                    binding.deliveryBoyInfoCard.setVisibility(GONE);
                                }
                            }
                        }
                    }
                    break;
            }
        });
    }
}