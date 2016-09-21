package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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


/**
 * Created by Eduardo on 24/07/2016.
 */
public class PointActivity extends AppCompatActivity {
    private ImageView pointImage;
    private TextView pointName;
    private TextView pointReview;
    private LinearLayout historicalEventsContainer;
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
        pointReview = (TextView) findViewById(R.id.point_review);
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
                Toast.makeText(getApplicationContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }
            try {
                Log.d("onPostExecute","asd");
                pointName.setText(point.getString("name"));
                pointReview.setText(point.getString("review"));
                Picasso.with(getApplicationContext()).load(Constants.POINTS_DIRECTORY+point.getString("file_name")).into(pointImage);
                int length = historicalEvents.length();
                if(length>0) {
                    for (int i = 0; i < length; i++) {
                        // Historical Event container
                        LinearLayout historicalEventContainer = new LinearLayout(getApplicationContext());
                        //historicalEventContainer.setOrientation(LinearLayout.HORIZONTAL);
                        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        //layoutParams.setMargins(0,0,10,0);
                        //historicalEventContainer.setLayoutParams(layoutParams);
                        // Date container
                        LinearLayout dateContainer = new LinearLayout(getApplicationContext());
                        dateContainer.setOrientation(LinearLayout.VERTICAL);
                        // Build the date format
                        TextView date = new TextView(getApplicationContext());
                        String dateBuilder = "";
                        if (historicalEvents.getJSONObject(i).getString("day").compareTo("null") != 0) {
                            dateBuilder += historicalEvents.getJSONObject(i).getString("day") + "/";
                        }
                        if (historicalEvents.getJSONObject(i).getString("month").compareTo("null") != 0) {
                            dateBuilder += historicalEvents.getJSONObject(i).getString("month") + "/";
                        }
                        if (historicalEvents.getJSONObject(i).getString("year").compareTo("null") != 0) {
                            dateBuilder += historicalEvents.getJSONObject(i).getString("year");
                        }
                        date.setText(dateBuilder);
                        TextView period = new TextView(getApplicationContext());
                        period.setText(historicalEvents.getJSONObject(i).getString("period"));
                        dateContainer.addView(date);
                        dateContainer.addView(period);
                        dateContainer.setPadding(0, 0, 20, 0);
                        // Add the date container
                        historicalEventContainer.addView(dateContainer);
                        // Title container
                        TextView title = new TextView(getApplicationContext());
                        title.setText(historicalEvents.getJSONObject(i).getString("title"));
                        title.setTextSize(11);
                        historicalEventContainer.addView(title);
                        // Arrow
                        ImageView arrow = new ImageView(getApplicationContext());
                        arrow.setImageResource(R.drawable.arrow_right);
                        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        arrowParams.gravity = Gravity.RIGHT;
                        arrow.setLayoutParams(arrowParams);
                        // Add elements to the container
                        historicalEventContainer.addView(arrow);
                        historicalEventContainer.setPadding(20, 20, 20, 20);
                        final int finalI = i;
                        historicalEventContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent historical_event = new Intent(PointActivity.this, HistoricalEventActivity.class);
                                try {
                                    historical_event.putExtra("historicalEventId", historicalEvents.getJSONObject(finalI).getString("id"));
                                    startActivity(historical_event);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(PointActivity.this, R.string.service_connection_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        // This is for adding the period divider
                        TextView periodDivider = new TextView(getApplicationContext());
                        Integer yearInt = Integer.parseInt(historicalEvents.getJSONObject(i).getString("year"));
                        switch (historicalEvents.getJSONObject(i).getString("period")) {
                            case "D.C.":
                                if (yearInt > 1400 && yearInt <= 1532) {
                                    if (!chronology.get("HORIZONTE_TARDIO")) {
                                        // If is first time on period
                                        String periodString = new String("Horizonte Tardio");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("HORIZONTE_TARDIO", true);
                                } else if (yearInt > 1000 && yearInt <= 1400) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TARDIO")) {
                                        // If is first time on period
                                        String periodString = new String("Periodo Intermedio Tardio");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TARDIO", true);
                                } else if (yearInt > 550 && yearInt <= 1000) {
                                    if (!chronology.get("HORIZONTE_MEDIO")) {
                                        // If is first time on period
                                        String periodString = new String("Horizonte Medio");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("HORIZONTE_MEDIO", true);
                                } else if (yearInt <= 550) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TEMPRANO")) {
                                        // If is first time on period
                                        String periodString = new String("Periodo Intermedio Temprano");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TEMPRANO", true);
                                }
                                break;
                            case "A.C.":
                                if (yearInt > 0 && yearInt <= 400) {
                                    if (!chronology.get("PERIODO_INTERMEDIO_TEMPRANO")) {
                                        // If is first time on period
                                        String periodString = new String("Periodo Intermedio Temprano");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("PERIODO_INTERMEDIO_TEMPRANO", true);
                                } else if (yearInt > 400 && yearInt <= 1400) {
                                    if (!chronology.get("HORIZONTE_TEMPRANO")) {
                                        // If is first time on period
                                        String periodString = new String("Horizonte Temprano");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("HORIZONTE_TEMPRANO", true);
                                } else if (yearInt > 1400 && yearInt <= 1800) {
                                    if (!chronology.get("PERIODO_INICIAL")) {
                                        // If is first time on period
                                        String periodString = new String("Periodo Inicial");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("PERIODO_INICIAL", true);
                                } else if (yearInt < 1800) {
                                    if (!chronology.get("PRECERAMICO")) {
                                        // If is first time on period
                                        String periodString = new String("Pre-ceramico");
                                        SpannableString content = new SpannableString(periodString);
                                        content.setSpan(new UnderlineSpan(), 0, periodString.length(), 0);
                                        periodDivider.setText(content);
                                        historicalEventsContainer.addView(periodDivider);
                                    }
                                    chronology.put("PRECERAMICO", true);
                                }
                                break;
                        }
                        // Divider
                        View divider = new View(getApplicationContext());
                        divider.setBackgroundColor(Color.GRAY);
                        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
                        historicalEventsContainer.addView(historicalEventContainer);
                        historicalEventsContainer.addView(divider);
                    }
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
