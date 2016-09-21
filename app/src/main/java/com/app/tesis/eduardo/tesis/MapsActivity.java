package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    double latitude;
    double longitude;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    private GoogleMap mMap;
    private Circle visionCircle;
    private Marker locationMarker;
    ProgressDialog mProgressDialog;
    private Bundle inBundle;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    JSONArray points;
    private Button buttonCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        m_ServiceAccess = new AccessServiceAPI();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_points_message));
        mProgressDialog.setTitle(R.string.progress_dialog_title);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Set the profile data
        setProfileData();
        // Set camera button
        setCameraButton();
    }

    public void setProfileData(){
        // Get profile
        inBundle = getIntent().getExtras();
        //profile = (Profile) inBundle.get("profile");
        login_method = (String) inBundle.get("login_method");
        if(login_method.equals("fb")){
            fbId = (String) inBundle.get("fbId");
        }
        userId = (Integer) inBundle.get("userId");
        fbFullname = (String) inBundle.get("fullname");
        fbEmail = (String) inBundle.get("email");
        post_as_anonymous = (Boolean) inBundle.get("post_as_anonymous");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        /*
        // Add a marker in current position
        LatLng currentPosition = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentPosition).title(String.valueOf(R.string.current_position)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        */
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            visionCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(100)
                    .strokeWidth(2)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(60, 157, 228, 239))
                    .clickable(true));
            /*
            locationCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(6)
                    .strokeWidth(0)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.BLUE));
            */
            locationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.current_position)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user_location)));
            mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    Toast.makeText(getApplicationContext(),R.string.augmented_reality_radius,Toast.LENGTH_LONG).show();
                }
            });
            /*
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(String.valueOf(R.string.current_position));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currLocationMarker = mMap.addMarker(markerOptions);
            */
            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            getPoints();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        /*
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        */
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        visionCircle.setCenter(latLng);
        locationMarker.setPosition(latLng);
        //locationCircle.setCenter(latLng);
        /*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(String.valueOf(R.string.current_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currLocationMarker = mMap.addMarker(markerOptions);
        */
        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();

        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    public void getPoints(){
        Log.d("GETU","getPoints()");
        Double latitudeUpperBound = latitude + 0.01;
        Double latitudeLowerBound = latitude - 0.01;
        Double longitudeUpperBound = longitude + 0.01;
        Double longitudeLowerBound = longitude - 0.01;
        new TaskGetNearestPoints(mProgressDialog,this).execute(String.valueOf(latitudeUpperBound),String.valueOf(latitudeLowerBound),String.valueOf(longitudeUpperBound),String.valueOf(longitudeLowerBound));
    }


    public class TaskGetNearestPoints extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        ProgressDialog progressDialog;
        MapsActivity activity;
        public TaskGetNearestPoints(ProgressDialog mProgressDialog, MapsActivity act){
            this.progressDialog = mProgressDialog;
            this.activity = act;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("latitude_upper_bound", params[0]);
            param.put("latitude_lower_bound", params[1]);
            param.put("longitude_upper_bound", params[2]);
            param.put("longitude_lower_bound", params[3]);

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_GET(Constants.ENDPOINT_URL+Constants.NEAREST_POINTS,param));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                points = jObjResult.getJSONArray("points");
                /*
                JSONArray jsonArray = jObjResult.getJSONArray("points");
                int length = jsonArray.length();
                for(int i=0;i<length;i++){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(jsonArray.getJSONObject(i).getDouble("latitude"), jsonArray.getJSONObject(i).getDouble("longitude"))).title(jsonArray.getJSONObject(i).getString("name")));
                }
                */
                return Constants.ENDPOINT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(result == Constants.ENDPOINT_ERROR){
                Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }else if(result == Constants.ENDPOINT_SUCCESS) {
                int length = points.length();
                try {
                    for (int i = 0; i < length; i++) {
                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(points.getJSONObject(i).getDouble("latitude"), points.getJSONObject(i).getDouble("longitude"))).title(points.getJSONObject(i).getString("name"));
                        switch(points.getJSONObject(i).getInt("point_type_id")){
                            case 1:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.casona));
                                break;
                            case 2:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.edificio));
                                break;
                            case 3:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.huaca));
                                break;
                            case 4:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.iglesia));
                                break;
                            case 5:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.monumento));
                                break;
                            case 6:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.plaza));
                                break;
                        }
                        mMap.addMarker(markerOptions);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            progressDialog.dismiss();
        }
    }

    public void setCameraButton(){
        buttonCamera = (Button)findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MapsActivity.this, CameraActivity.class);
                camera.putExtra("login_method",login_method);
                if(login_method.equals("fb")) {
                    camera.putExtra("fbId", fbId);
                }
                camera.putExtra("userId",userId);
                camera.putExtra("fullname",fbFullname);
                camera.putExtra("email",fbEmail);
                camera.putExtra("post_as_anonymous",post_as_anonymous);
                startActivity(camera);
            }
        });
    }
}
