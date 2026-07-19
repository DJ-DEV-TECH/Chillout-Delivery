package com.app.chillout_delivery.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.chillout_delivery.R;
import com.app.chillout_delivery.base.BaseActivity;
import com.app.chillout_delivery.databinding.ActivityOrderDetailsBinding;
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
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetailsActivity extends BaseActivity implements OnMapReadyCallback {

    private ActivityOrderDetailsBinding binding;
    private GoogleMap mMap;
    Marker riderMarker;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        binding.shimmerCard.setVisibility(View.VISIBLE);
        binding.mapShimmerLayout.startShimmer();

        binding.orderStatusBtn.setOnClickListener(v -> {

        });

        binding.naviBtn.setOnClickListener(v -> {
            simpleNavi();
        });
    }

    private void simpleNavi() {
        double latitude = 11.447009;   // your lat
        double longitude = 77.702241;  // your lng

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude+"&mode=d");
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

        LatLng restaurant = new LatLng(11.438407, 77.701524); // Chennai
        LatLng customer = new LatLng(11.447009, 77.702241);

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + restaurant.latitude + "," + restaurant.longitude +
                        "&destination=" + customer.latitude + "," + customer.longitude +
                        "&key=AIzaSyBRcTL0kqdN1xpQzW2Kq4zr5nm6veWAek8";

        Bitmap circleBitmap = createCustomMarker(this, R.drawable.vijay_image);
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
}