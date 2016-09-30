package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
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

import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;


/**
 * Created by Eduardo on 24/07/2016.
 */
public class PointActivity extends AppCompatActivity {
    private ImageView pointImage;
    private TextView pointName;
    private WebView pointReview;
    private LinearLayout historicalEventsContainer;
    private LinearLayout heContainer;
    private Button buttonAddHistoricalEvent;
    Map<String, Boolean> chronology;
    private Bundle inBundle;
    Integer userId;
    String fbId;
    String fbFullname;
    String fbEmail;
    String login_method;
    Boolean post_as_anonymous;
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
        pointReview = (WebView) findViewById(R.id.point_review);
        historicalEventsContainer = (LinearLayout) findViewById(R.id.historical_events_container);
        setProfileData();
        // Set add historical event button
        setAddHistoricalEventButton();
        m_ServiceAccess = new AccessServiceAPI();
        chronology = new HashMap<>();
        setChronology();
        new TaskGetPoint().execute();
    }

    public void setProfileData(){
        // Get profile
        inBundle = getIntent().getExtras();
        pointId = (Long) inBundle.get("pointId");
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

    public void setAddHistoricalEventButton(){
        buttonAddHistoricalEvent = (Button)findViewById(R.id.button_add_historical_event);
        buttonAddHistoricalEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testimony = new Intent(PointActivity.this, TestimonyActivity.class);
                testimony.putExtra("login_method",login_method);
                if(login_method.equals("fb")) {
                    testimony.putExtra("fbId", fbId);
                }
                testimony.putExtra("pointId",pointId);
                testimony.putExtra("userId",userId);
                testimony.putExtra("fullname",fbFullname);
                testimony.putExtra("email",fbEmail);
                testimony.putExtra("post_as_anonymous",post_as_anonymous);
                startActivity(testimony);
            }
        });
    }

    public void setChronology(){
        chronology.put("HORIZONTE_TARDIO",false);
        chronology.put("PERIODO_INTERMEDIO_TARDIO",false);
        chronology.put("HORIZONTE_MEDIO",false);
        chronology.put("PERIODO_INTERMEDIO_TEMPRANO",false);
        chronology.put("HORIZONTE_TEMPRANO",false);
        chronology.put("PERIODO_INICIAL",false);
        chronology.put("PRECERAMICO",false);
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
                centeredToast(getApplicationContext(),getString(R.string.service_connection_error));
                //Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
            try {
                Log.d("onPostExecute","asd");
                pointName.setText(point.getString("name"));
                String htmlText = "<html lang=\"es\"><body style=\"text-align:justify;background-color:#303030;color:#c1c1c1\"> %s </body></Html>";
                pointReview.loadData(String.format(htmlText, point.getString("review")),"text/html; charset=utf-8", "utf-8");
                //pointReview.setText(point.getString("review"));
                Picasso.with(getApplicationContext()).load(Constants.POINTS_DIRECTORY+point.getString("file_name")).into(pointImage);
                int length = historicalEvents.length();
                if(length>0) {
                    // Begin the transaction
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    for (int i = 0; i < length; i++) {
                        Integer yearInt = Integer.parseInt(historicalEvents.getJSONObject(i).getString("year"));
                        String chronologyStr = "null";
                        switch (historicalEvents.getJSONObject(i).getString("period")) {
                            case "D.C.":
                                if (yearInt > 1400 && yearInt <= 1532) {
                                    if (!chronology.get("HORIZONTE_TARDIO")) {
                                        // If is first time on period
                                        chronologyStr = "Horizonte Tardio";
                                    }
                                    chronology.put("HORIZONTE_TARDIO", true);
                                } else if (yearInt > 1000 && yearInt <= 1400) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TARDIO")) {
                                        // If is first time on period
                                        chronologyStr = "Periodo Intermedio Tardio";
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TARDIO", true);
                                } else if (yearInt > 550 && yearInt <= 1000) {
                                    if (!chronology.get("HORIZONTE_MEDIO")) {
                                        // If is first time on period
                                        chronologyStr = "Horizonte Medio";
                                    }
                                    chronology.put("HORIZONTE_MEDIO", true);
                                } else if (yearInt <= 550) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TEMPRANO")) {
                                        // If is first time on period
                                        chronologyStr = "Periodo Intermedio Temprano";
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TEMPRANO", true);
                                }
                                break;
                            case "A.C.":
                                if (yearInt > 0 && yearInt <= 400) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TEMPRANO")) {
                                        // If is first time on period
                                        chronologyStr = "Periodo Intermedio Temprano";
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TEMPRANO", true);
                                } else if (yearInt > 400 && yearInt <= 1400) {
                                    if (!chronology.get("HORIZONTE_TEMPRANO")) {
                                        // If is first time on period
                                        chronologyStr = "Horizonte Temprano";
                                    }
                                    chronology.put("HORIZONTE_TEMPRANO", true);
                                } else if (yearInt > 1400 && yearInt <= 1800) {
                                    if (!chronology.get("PERIODO_INICIAL")) {
                                        // If is first time on period
                                        chronologyStr = "Periodo Inicial";
                                    }
                                    chronology.put("PERIODO_INICIAL", true);
                                } else if (yearInt < 1800) {
                                    if (!chronology.get("PRECERAMICO")) {
                                        // If is first time on period
                                        chronologyStr = "Pre-ceramico";
                                    }
                                    chronology.put("PRECERAMICO", true);
                                }
                                break;
                        }
                        HistoricalEventFragment heFragment = new HistoricalEventFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("chronology",chronologyStr);
                        bundle.putString("day",historicalEvents.getJSONObject(i).getString("day"));
                        bundle.putString("month",historicalEvents.getJSONObject(i).getString("month"));
                        bundle.putString("year",historicalEvents.getJSONObject(i).getString("year"));
                        bundle.putString("period",historicalEvents.getJSONObject(i).getString("period"));
                        bundle.putString("historicalEventTitle",historicalEvents.getJSONObject(i).getString("title"));
                        bundle.putString("historicalEventId",historicalEvents.getJSONObject(i).getString("id"));
                        bundle.putString("userId", String.valueOf(userId));
                        heFragment.setArguments(bundle);
                        ft.add(R.id.historical_events_container, heFragment);
                    }
                    ft.commit();
                }else{
                    TextView emptyResults = new TextView(getApplicationContext());
                    emptyResults.setText(R.string.historical_events_empty_result);
                    emptyResults.setGravity(Gravity.CENTER);
                    historicalEventsContainer.addView(emptyResults);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
