package com.app.tesis.eduardo.tesis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

/**
 * Created by Eduardo on 27/09/2016.
 */

public class TestimonyFragment extends Fragment {

    private Integer userId;
    LinearLayout testimonialsContainer;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    JSONArray testimonials;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getInt("userId");
        m_ServiceAccess = new AccessServiceAPI();
        //Toast.makeText(this.getContext(),historicalEventId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mc_testominy, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        testimonialsContainer = (LinearLayout) getView().findViewById(R.id.testimonials_container);
        new TaskMCTestimonials().execute(String.valueOf(userId));

    }

    public class TaskMCTestimonials extends AsyncTask<String, Void, Integer> {
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
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.CITIZENS+params[0]+"/testimonials"));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                testimonials = jObjResult.getJSONArray("testimonials");
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
                //Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }else if(result == Constants.ENDPOINT_SUCCESS){
                try {
                    int length = testimonials.length();
                    if(length>0){
                        // Begin the transaction
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        for(int i=0;i<length;i++){
                            TestimonyItemFragment tiFragment = new TestimonyItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("day",testimonials.getJSONObject(i).getString("day"));
                            bundle.putString("month",testimonials.getJSONObject(i).getString("month"));
                            bundle.putString("year",testimonials.getJSONObject(i).getString("year"));
                            bundle.putString("period",testimonials.getJSONObject(i).getString("period"));
                            bundle.putString("testimonyTitle",testimonials.getJSONObject(i).getString("title"));
                            bundle.putString("testimonyEventId",testimonials.getJSONObject(i).getString("id"));
                            bundle.putString("userId", String.valueOf(userId));
                            tiFragment.setArguments(bundle);
                            ft.add(R.id.testimonials_container, tiFragment);
                        }
                        ft.commit();
                    }else{
                        TextView emptyResults = new TextView(getContext());
                        emptyResults.setText(R.string.empty_result);
                        emptyResults.setGravity(Gravity.CENTER);
                        testimonialsContainer.addView(emptyResults);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
