package mobile.ui.dashboard;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import io.github.sceneview.sample.arcursorplacement.R;
import io.github.sceneview.sample.arcursorplacement.databinding.FragmentDashboardBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private AppCompatActivity activity;

    private FragmentDashboardBinding binding;

    private GoogleMap map;

    String model = null;

//    ListData listData;

    ListAdapter listAdapter;

    ArrayList<ArrayList<ListData>> markerInfo= new ArrayList<ArrayList<ListData>>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.listView.setAdapter(listAdapter);
        binding.listView.setClickable(true);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(this);

        // Return view
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

//
//        map.setOnMarkerClickListener(this);
//
//        ArrayList<ListData> yourData = new ArrayList<>();
//        int[] imageList = new int[]{R.drawable.bed, R.drawable.bed, R.drawable.bed};
//        String[] addressList = new String[]{"XXXX", "XXXX", "XXXX"};
//
//        for (int i = 0; i < imageList.length; i++) {
//            listData = new com.example.mobile.ui.dashboard.ListData(addressList[i],imageList[i]);
//            yourData.add(listData);
//        }
//
//        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext(),yourData));
//        map.addMarker(markerOptions);
//
//
//
//
//
//
//        markerLocation = new LatLng(37.425, -122.085);
//
//        markerOptions = new MarkerOptions()
//                .position(markerLocation) // Marker position
//                .title("3"); // Marker title
//



        map.setOnMarkerClickListener(this);

//        yourData = new ArrayList<>();
//        imageList = new int[]{R.drawable.bed, R.drawable.bed, R.drawable.bed};
//        addressList = new String[]{"1309,31 Abeckett st", "1408,31 Abeckett st", "307,31 Abeckett st"};
//
//        for (int i = 0; i < imageList.length; i++) {
//            listData = new com.example.mobile.ui.dashboard.ListData(addressList[i],imageList[i]);
//            yourData.add(listData);
//        }
//
//        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext(),yourData));
//        map.addMarker(markerOptions);
//
//        listAdapter = new ListAdapter(requireContext(), yourData);
//        binding.listView.setAdapter(listAdapter);
//        binding.listView.setClickable(true);
//
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.listView.getLayoutParams();
//
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//
//        params.leftMargin = 100; // Left margin
//
//        binding.listView.setLayoutParams(params);

        //map.addMarker(markerOptions).showInfoWindow();



        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // send Coordinates to server
                        sendCoordinates(currentLocation);}}  });
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // hide ListView
                binding.listView.setVisibility(View.GONE);
                //super.onMapClick(latLng);
            }
        });

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                // hide ListView
                binding.listView.setVisibility(View.GONE);

            }
        });


    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        String originalTitle = marker.getTitle();
        ArrayList<ListData> dataItem = markerInfo.get(Integer.parseInt(originalTitle));

        listAdapter = new ListAdapter(requireContext(), dataItem);
        binding.listView.setAdapter(listAdapter);
        binding.listView.setClickable(true);
        Projection projection = map.getProjection();

        LatLng markerLatLng = marker.getPosition();


        Point screenPosition = projection.toScreenLocation(markerLatLng);

        // screenPosition 包含了标记在屏幕上的位置
        int x = screenPosition.x;
        int y = screenPosition.y;

        // 现在你可以使用 x 和 y 来获取标记在屏幕上的具体位置

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.listView.getLayoutParams();

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        int distanceHorizontal = params.rightMargin+params.leftMargin;
        params.leftMargin = x; // Left margin
        params.rightMargin = distanceHorizontal-params.leftMargin; // Right margin
        int distanceVertical = params.topMargin+params.bottomMargin;
        params.topMargin = y; // Top margin
        params.bottomMargin = distanceVertical-params.topMargin; // Bottom margin

        binding.listView.setLayoutParams(params);
        binding.listView.setClickable(true);
        binding.listView.setVisibility(View.VISIBLE);


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestModel(dataItem.get(i).address);
//                Intent intent = new Intent(packageContext:MainActivity.this, DetailedActivity.class;
//                startActivity(intent);
            } });
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestPermission(activity, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
    }


    public boolean onMyLocationButtonClick() {
        Toast.makeText(requireContext(), "Locating Nearby Properties...", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(requireContext(), "My location:\n" + location, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    private void sendCoordinates(LatLng currentLocation) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://mobiles-2a62216dada4.herokuapp.com/location/na";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("requestTest", "Response: " + response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ArrayList<ListData> dataItems = new ArrayList<>();
                                JSONObject data = jsonArray.getJSONObject(i);

                                double latitude = data.getDouble("latitude");
                                double longitude = data.getDouble("longitude");
                                int number=data.getInt("number");

                                JSONArray propertiesArray = data.getJSONArray("properties");
                                for (int j = 0; j < propertiesArray.length(); j++) {
                                    JSONObject property = propertiesArray.getJSONObject(j);
                                    String address = property.getString("address");
                                    String id = property.getString("id");
                                    String imgB64 = property.getString("image");
                                    byte[] decodeImage = Base64.decode(imgB64, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.length);
                                    ListData modelItem = new ListData(address, bitmap);
                                    dataItems.add(modelItem);
                                    Log.e("requestTest", "number: " + dataItems.size());
                                }

                                // location
                                LatLng location = new LatLng(latitude, longitude);

                                // marker
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(location)
                                        .title(String.valueOf(i));

                                map.addMarker(markerOptions);
                                markerInfo.add(dataItems);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("requestTest", "Error: " + error.getMessage());
                        // deal with error response
                    }
                })  {
            @Override
            public byte[] getBody() {
                JSONObject coordinates = new JSONObject();
                try {
                    coordinates.put("latitude", currentLocation.latitude);
                    coordinates.put("longitude", currentLocation.longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject params = new JSONObject();
                try {
                    params.put("coordinates", coordinates);
                    Log.e("requestTest", "message: " + params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // add to queue
        queue.add(stringRequest);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getChildFragmentManager(), "dialog");
    }




    private void requestModel(String address) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://mobiles-2a62216dada4.herokuapp.com/location/layout";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("requestTest", "Model: " + response.toString().substring(0, Math.min(response.toString().length(), 50)));

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            model = jsonObject.getString("model");

                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("requestTest", "Error: " + error.getMessage());
                        // deal with error response
                    }
                })  {
            @Override
            public byte[] getBody() {

                JSONObject params = new JSONObject();
                try {
                    params.put("address", address);
                    Log.e("requestTest", "message: " + params.toString().getBytes());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params.toString().getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        // add to queue
        queue.add(stringRequest);
    }
}