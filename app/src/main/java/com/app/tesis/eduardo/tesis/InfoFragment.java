package com.app.tesis.eduardo.tesis;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

/**
 * Created by Eduardo on 8/09/2016.
 */
public class InfoFragment extends Fragment {
    private String historicalEventId;
    TextView he_title_txt;
    TextView he_date_txt;
    WebView he_review_txt;
    String he_title;
    String he_date;
    String he_review;
    String day;
    String month;
    String year;
    String period;
    Integer userId;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicalEventId = getArguments().getString("historicalEventId");
        userId = getArguments().getInt("userId");
        m_ServiceAccess = new AccessServiceAPI();
        //Toast.makeText(this.getContext(),historicalEventId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_he_info, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        he_title_txt = (TextView)getView().findViewById(R.id.he_title_txt);
        he_date_txt = (TextView)getView().findViewById(R.id.he_date_txt);
        he_review_txt = (WebView)getView().findViewById(R.id.he_review_txt);
        new TaskHEInfo().execute(historicalEventId);
    }

    public class TaskHEInfo extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();

            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.HISTORICAL_EVENTS+params[0]));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                he_title = jObjResult.getJSONObject("historical_event").getString("title");
                he_review = jObjResult.getJSONObject("historical_event").getString("description");
                day = jObjResult.getJSONObject("historical_event").getString("day");
                month = jObjResult.getJSONObject("historical_event").getString("month");
                year = jObjResult.getJSONObject("historical_event").getString("year");
                period = jObjResult.getJSONObject("historical_event").getString("period");
                String dateBuilder = "";
                if(day.compareTo("null")!= 0){
                    dateBuilder += day+"/";
                }
                if(month.compareTo("null")!= 0){
                    dateBuilder += month+"/";
                }
                if(year.compareTo("null")!= 0){
                    dateBuilder += year;
                }
                if(period.compareTo("null")!= 0){
                    dateBuilder += " - "+period;
                }
                he_date = dateBuilder;
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
                centeredToast(getContext(),getString(R.string.service_connection_error));
                //Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_LONG).show();
            }else if(result == Constants.ENDPOINT_SUCCESS){
                he_title_txt.setText(he_title);
                he_date_txt.setText(he_date);
                //he_review_txt.setText(he_review);
                String htmlText = "<html lang=\"es\"><body style=\"text-align:justify;background-color:#303030;color:#c1c1c1\"> %s </body></Html>";
                he_review_txt.loadData(String.format(htmlText, he_review),"text/html; charset=utf-8", "utf-8");
            }
        }
    }
}
