package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.AsyncResponse;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.beyondar.android.world.GeoObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Eduardo on 24/07/2016.
 */
public class PointActivity extends AppCompatActivity {
    private Bundle inBundle;
    private ImageView pointImage;
    private TextView pointName;
    private TextView pointReview;
    private LinearLayout historicalEventsContainer;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    private Long pointId;
    private JSONObject point;
    private JSONArray historicalEvents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        pointImage = (ImageView) findViewById(R.id.point_image);
        pointName = (TextView) findViewById(R.id.point_name);
        pointReview = (TextView) findViewById(R.id.point_review);
        historicalEventsContainer = (LinearLayout) findViewById(R.id.historical_events_container);
        m_ServiceAccess = new AccessServiceAPI();
        inBundle = getIntent().getExtras();
        pointId = (Long) inBundle.get("pointId");
        new TaskGetPoint().execute();
    }

    public class TaskGetPoint extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        private ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.POINTS+String.valueOf(pointId)));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                point = jObjResult.getJSONObject("point");
                historicalEvents = jObjResult.getJSONArray("historical_events");
                Log.d("RESULT", String.valueOf(jObjResult));
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
            }
            try {
                Log.d("onPostExecute","asd");
                pointName.setText(point.getString("name"));
                pointReview.setText(point.getString("review"));
                Picasso.with(getApplicationContext()).load(Constants.POINTS_DIRECTORY+point.getString("file_name")).into(pointImage);
                int length = historicalEvents.length();
                for(int i=0;i<length;i++){
                    LinearLayout historicalEventContainer = new LinearLayout(getApplicationContext());
                    historicalEventContainer.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout dateContainer = new LinearLayout(getApplicationContext());
                    dateContainer.setOrientation(LinearLayout.VERTICAL);

                    TextView date = new TextView(getApplicationContext());
                    String dateBuilder = "";
                    if(historicalEvents.getJSONObject(i).getString("day").length() > 0){
                        dateBuilder += historicalEvents.getJSONObject(i).getString("day")+"/";
                    }
                    if(historicalEvents.getJSONObject(i).getString("month").length() > 0){
                        dateBuilder += historicalEvents.getJSONObject(i).getString("month")+"/";
                    }
                    if(historicalEvents.getJSONObject(i).getString("year").length() > 0){
                        dateBuilder += historicalEvents.getJSONObject(i).getString("year");
                    }
                    date.setText(dateBuilder);
                    TextView period = new TextView(getApplicationContext());
                    period.setText(historicalEvents.getJSONObject(i).getString("period"));
                    period.setGravity(Gravity.CENTER_HORIZONTAL);
                    dateContainer.addView(date);
                    dateContainer.addView(period);

                    historicalEventContainer.addView(dateContainer);

                    TextView title = new TextView(getApplicationContext());
                    title.setText(historicalEvents.getJSONObject(i).getString("title"));
                    title.setTextSize(16);
                    title.setGravity(Gravity.CENTER);
                    historicalEventContainer.addView(title);
                    historicalEventsContainer.addView(historicalEventContainer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
