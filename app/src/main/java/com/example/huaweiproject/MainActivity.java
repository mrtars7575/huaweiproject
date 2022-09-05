package com.example.huaweiproject;

import static java.util.Locale.SIMPLIFIED_CHINESE;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;


import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Route Planning
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "-------->";

    private SupportMapFragment mSupportMapFragment;

    private HuaweiMap hMap;

    private Marker mMarkerOrigin;

    private Marker mMarkerDestination;

    private EditText edtOriginLat;

    private EditText edtOriginLng;

    private EditText edtDestinationLat;

    private EditText edtDestinationLng;

    private TextView timeTv;

    private TextView distanceTv;

    private String distanceText;

    private String durationText;

    //private LatLng latLng1 = new LatLng(54.216608, -4.66529);

    private LatLng latLng1;

    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mProviderClient;

    private LatLng latLng2 = new LatLng(40.9912, 29.1);

    private List<Polyline> mPolylines = new ArrayList<>();

    private List<List<LatLng>> mPaths = new ArrayList<>();

    private LatLngBounds mLatLngBounds;

    private LocationCallback mLocationCallback;

    private SettingsClient mSettingsClient;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    renderRoute(mPaths, mLatLngBounds);
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    System.out.println("error message --> " + errorMsg);
                    //Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        MapsInitializer.setApiKey("DAEDADBfDgUVHydgn/rZGmpU7Sw4nGraSNn1rA4rfcYD59JG1XxAztkZCvdV4AnplUsEKyu8UslA9+MYc6zxQAl9t29QSYs97hfhgQ==");
        setContentView(R.layout.activity_main);

        initLocationRequest();
        requestLocationUpdates();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mapfragment_routeplanningdemo);
        if (fragment instanceof SupportMapFragment) {
            mSupportMapFragment = (SupportMapFragment) fragment;
            mSupportMapFragment.getMapAsync(this);
        }
        edtOriginLat = findViewById(R.id.edt_origin_lat);
        edtOriginLng = findViewById(R.id.edt_origin_lng);
        edtDestinationLat = findViewById(R.id.edt_destination_lat);
        edtDestinationLng = findViewById(R.id.edt_destination_lng);
        timeTv = findViewById(R.id.timeTv);
        distanceTv = findViewById(R.id.distanceTv);
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        //Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(10000);
        //Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private LocationCallback initLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if (null != locations && locations.size() > 0) {
                        Location location = locations.get(0);
                        final double  longitude = location.getLongitude();
                        final double latitude = location.getLatitude();



                        System.out.println("longitude :" + longitude + "latittude  : " + latitude);
                        // Enable the subthread to invoke the reverse geocoding capability to obtain location information.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Reverse geocoding address
                                    final Geocoder geocoder = new Geocoder(MainActivity.this, SIMPLIFIED_CHINESE);
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    // After the address information is updated successfully, use the handler to update the UI.
                                    if (null != addresses && addresses.size() > 0) {
                                        for (Address address : addresses) {
                                           /* Message msg = new Message();
                                            msg.what = GET_DETAIL_ADDRESS;
                                            msg.obj = addresses.get(0).getFeatureName();
                                            handler.sendMessage(msg);
*/                                          System.out.println("latitude : " + latitude + " longitude" + longitude);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {



                                                    //LatLng latLng = new LatLng(latitude,longitude);
                                                    latLng1 = new LatLng(latitude,longitude);
                                                    hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 13));
                                                    addOriginMarker(latLng1);
                                                }
                                            });

                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("reverseGeocode wrong ");
                                }
                            }
                        }).start();


                    }
                }
            }
        };
        return mLocationCallback;
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;


        addDestinationMarker(latLng2);
    }

    private void requestLocationUpdates() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        mProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Check the device location settings.
        mSettingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Set the location conditions to be met and then initiate a location request.
                        mProviderClient.requestLocationUpdates(mLocationRequest, initLocationCallback(), Looper.getMainLooper())
                                // Listening callback for successful location update request interface
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("The callback is executed successfully.");
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                int statusCode = ((ApiException) e).getStatusCode();
                                                switch (statusCode) {
                                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                        try {
                                                            ResolvableApiException rae = (ResolvableApiException) e;
                                                            rae.startResolutionForResult(MainActivity.this, 0);
                                                        } catch (IntentSender.SendIntentException sie) {
                                                            System.out.println("sie=" + sie.getMessage());
                                                        }
                                                        break;
                                                }
                                            }
                                        }
                );
    }

    public void getWalkingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getWalkingRoutePlanningResult(latLng1, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void getBicyclingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getBicyclingRoutePlanningResult(latLng1, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        Log.d("sfj", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    public void getDrivingRouteResult(View view) {
        removePolylines();
        NetworkRequestManager.getDrivingRoutePlanningResult(latLng1, latLng2,
                new NetworkRequestManager.OnNetworkListener() {
                    @Override
                    public void requestSuccess(String result) {
                        generateRoute(result);
                    }

                    @Override
                    public void requestFail(String errorMsg) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("errorMsg", errorMsg);
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void generateRoute(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.optJSONArray("routes");
            if (null == routes || routes.length() == 0) {
                return;
            }
            JSONObject route = routes.getJSONObject(0);

            // get route bounds
            JSONObject bounds = route.optJSONObject("bounds");
            if (null != bounds && bounds.has("southwest") && bounds.has("northeast")) {
                JSONObject southwest = bounds.optJSONObject("southwest");
                JSONObject northeast = bounds.optJSONObject("northeast");
                LatLng sw = new LatLng(southwest.optDouble("lat"), southwest.optDouble("lng"));
                LatLng ne = new LatLng(northeast.optDouble("lat"), northeast.optDouble("lng"));
                mLatLngBounds = new LatLngBounds(sw, ne);
            }

            // get paths
            JSONArray paths = route.optJSONArray("paths");
            for (int i = 0; i < paths.length(); i++) {
                JSONObject path = paths.optJSONObject(i);
                List<LatLng> mPath = new ArrayList<>();

                JSONArray steps = path.optJSONArray("steps");
                distanceText = path.getString("distanceText");
                distanceTv.setText("Distance : " + distanceText);
                System.out.println("distance text : " + distanceText);
                durationText = path.getString("durationText");
                timeTv.setText("Duration : " + durationText);

                System.out.println("duration text : " + durationText );
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.optJSONObject(j);

                    JSONArray polyline = step.optJSONArray("polyline");
                    for (int k = 0; k < polyline.length(); k++) {
                        if (j > 0 && k == 0) {
                            continue;
                        }
                        JSONObject line = polyline.getJSONObject(k);
                        double lat = line.optDouble("lat");
                        double lng = line.optDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        mPath.add(latLng);
                    }
                }
                mPaths.add(i, mPath);
            }
            mHandler.sendEmptyMessage(0);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException" + e.toString());
        }
    }

    /**
     * Render the route planning result
     *
     * @param paths paths
     * @param latLngBounds latLngBounds
     */
    private void renderRoute(List<List<LatLng>> paths, LatLngBounds latLngBounds) {
        if (null == paths || paths.size() <= 0 || paths.get(0).size() <= 0) {
            return;
        }

        for (int i = 0; i < paths.size(); i++) {
            List<LatLng> path = paths.get(i);
            PolylineOptions options = new PolylineOptions();
            for (LatLng latLng : path) {
                options.add(latLng);
            }

            Polyline polyline = hMap.addPolyline(options);
            mPolylines.add(i, polyline);
        }

        addOriginMarker(paths.get(0).get(0));
        addDestinationMarker(paths.get(0).get(paths.get(0).size() - 1));

        if (null != latLngBounds) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 5);
            hMap.moveCamera(cameraUpdate);
        } else {
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paths.get(0).get(0), 13));
        }

    }

    public void setOrigin(View view) {
        String mOriginLat = edtOriginLat.getText().toString().trim();
        String mOriginLng = edtOriginLng.getText().toString().trim();
        if (!TextUtils.isEmpty(mOriginLat) && !TextUtils.isEmpty(mOriginLng)) {
            try {
                latLng1 = new LatLng(Double.valueOf(mOriginLat), Double.valueOf(mOriginLng));

                removePolylines();
                addOriginMarker(latLng1);
                hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 13));
                mMarkerOrigin.showInfoWindow();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "IllegalArgumentException " + e);
                Toast.makeText(this, "IllegalArgumentException", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException " + e);
                Toast.makeText(this, "NullPointerException", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setDestination(View view) {
        String mDestinationLat = edtDestinationLat.getText().toString().trim();
        String mDestinationLng = edtDestinationLng.getText().toString().trim();
        if (!TextUtils.isEmpty(mDestinationLat) && !TextUtils.isEmpty(mDestinationLng)) {
            try {
                latLng2 = new LatLng(Double.valueOf(mDestinationLat), Double.valueOf(mDestinationLng));

                removePolylines();
                addDestinationMarker(latLng2);
                hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 13));
                mMarkerDestination.showInfoWindow();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "IllegalArgumentException " + e);
                Toast.makeText(this, "IllegalArgumentException", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException " + e);
                Toast.makeText(this, "NullPointerException", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addOriginMarker(LatLng latLng) {
        if (null != mMarkerOrigin) {
            mMarkerOrigin.remove();
        }
        mMarkerOrigin = hMap.addMarker(new MarkerOptions().position(latLng)
                .anchor(0.5f, 0.9f)
                // .anchorMarker(0.5f, 0.9f)
                .title("Origin")
                .snippet(latLng.toString()));
    }

    private void addDestinationMarker(LatLng latLng) {
        if (null != mMarkerDestination) {
            mMarkerDestination.remove();
        }
        mMarkerDestination = hMap.addMarker(
                new MarkerOptions().position(latLng).anchor(0.5f, 0.9f).title("Destination").snippet(latLng.toString()));
    }

    private void removePolylines() {
        for (Polyline polyline : mPolylines) {
            polyline.remove();
        }

        mPolylines.clear();
        mPaths.clear();
        mLatLngBounds = null;
    }

}