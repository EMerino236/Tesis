package com.app.tesis.eduardo.tesis;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;


/**
 * Created by Eduardo on 15/07/2016.
 */
public class CameraActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnClickBeyondarObjectListener {
    private OnClickBeyondarObjectListener myself = this;
    private BeyondarFragmentSupport mBeyondarFragment;
    double latitude;
    double longitude;
    World world;
    ProgressDialog mProgressDialog;
    private Bundle inBundle;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;
    /* Google services */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 0.5 sec
    private static int FATEST_INTERVAL = 100; // 0.1 sec
    private static int DISPLACEMENT = 1; // 1 meter
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    private Boolean hasPoints = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // Beyondar World
        world = new World(getApplicationContext());

        // Webservice
        m_ServiceAccess = new AccessServiceAPI();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_points_message));
        mProgressDialog.setTitle(R.string.progress_dialog_title);
        // Location manager
        mLocationManager = (LocationManager) getSystemService( getApplicationContext().LOCATION_SERVICE );
        // Lets check if the GPS is enabled
        if(!mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            centeredToast(getApplicationContext(),getString(R.string.gps_not_enabled));
            //Toast.makeText(getApplicationContext(), R.string.gps_not_enabled, Toast.LENGTH_LONG).show();
            finish();
        }
        // Set the profile data
        setProfileData();
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();

            displayLocation();

            togglePeriodicLocationUpdates();
        }
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        //mBeyondarFragment.getCameraView().setRotation(90);
        // User position (you can change it using the GPS listeners form Android API)
        world.setDefaultBitmap(R.drawable.marker_icon, 0);
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
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                centeredToast(getApplicationContext(),getString(R.string.device_not_supported));
                //Toast.makeText(getApplicationContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            world.setGeoPosition(latitude, longitude);
            if(!hasPoints){
                getPoints();
                hasPoints = true;
            }
        }

    }

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
        }
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Log.i("CAMERA-ACTIVITY", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        displayLocation();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        // Displaying the new location on UI
        displayLocation();
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
        // The first element in the array belongs to the closest BeyondarObject
        Intent point = new Intent(CameraActivity.this, PointActivity.class);
        point.putExtra("pointId",arrayList.get(0).getId());
        point.putExtra("login_method",login_method);
        if(login_method.equals("fb")) {
            point.putExtra("fbId", fbId);
        }
        point.putExtra("userId",userId);
        point.putExtra("fullname",fbFullname);
        point.putExtra("email",fbEmail);
        point.putExtra("post_as_anonymous",post_as_anonymous);
        startActivity(point);
    }

    public void getPoints(){
        Double latitudeUpperBound = latitude + 0.01;
        Double latitudeLowerBound = latitude - 0.01;
        Double longitudeUpperBound = longitude + 0.01;
        Double longitudeLowerBound = longitude - 0.01;
        new TaskGetNearestPoints(mProgressDialog,this).execute(String.valueOf(latitudeUpperBound),String.valueOf(latitudeLowerBound),String.valueOf(longitudeUpperBound),String.valueOf(longitudeLowerBound));
    }


    public class TaskGetNearestPoints extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        ProgressDialog progressDialog;
        CameraActivity activity;
        public TaskGetNearestPoints(ProgressDialog mProgressDialog, CameraActivity act){
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
                JSONArray jsonArray = jObjResult.getJSONArray("points");
                int length = jsonArray.length();
                for(int i=0;i<length;i++){
                    Log.d("ITERATOR",jsonArray.getJSONObject(i).getString("name"));
                    GeoObject go = new GeoObject(jsonArray.getJSONObject(i).getInt("id"));
                    go.setGeoPosition(jsonArray.getJSONObject(i).getDouble("latitude"), jsonArray.getJSONObject(i).getDouble("longitude"));
                    switch(jsonArray.getJSONObject(i).getInt("point_type_id")){
                        case 1:
                            go.setImageResource(R.drawable.casona);
                            break;
                        case 2:
                            go.setImageResource(R.drawable.edificio);
                            break;
                        case 3:
                            go.setImageResource(R.drawable.huaca);
                            break;
                        case 4:
                            go.setImageResource(R.drawable.iglesia);
                            break;
                        case 5:
                            go.setImageResource(R.drawable.monumento);
                            break;
                        case 6:
                            go.setImageResource(R.drawable.plaza);
                            break;
                        default:
                            go.setImageResource(R.drawable.marker_icon);
                            break;
                    }
                    go.setName(jsonArray.getJSONObject(i).getString("name"));
                    world.addBeyondarObject(go);
                }
                mBeyondarFragment.setWorld(world);
                mBeyondarFragment.setOnClickBeyondarObjectListener(myself);
                return Constants.ENDPOINT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result == Constants.ENDPOINT_ERROR){
                centeredToast(getApplicationContext(),getString(R.string.service_connection_error));
                //Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
