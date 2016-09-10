package com.app.tesis.eduardo.tesis;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Eduardo on 9/09/2016.
 */
public class PhotoFragment extends Fragment {
    private String historicalEventId;
    private LinearLayout photosContainer;
    private JSONArray photos;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicalEventId = getArguments().getString("historicalEventId");
        m_ServiceAccess = new AccessServiceAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_he_photo, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        photosContainer = (LinearLayout)getView().findViewById(R.id.photos_container);
        new TaskHEPhotos().execute(historicalEventId);
    }

    public class TaskHEPhotos extends AsyncTask<String, Void, Integer> {
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
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.HISTORICAL_EVENTS+params[0]+"/photos"));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                photos = jObjResult.getJSONArray("photos");
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
                Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_SHORT).show();
            }else if(result == Constants.ENDPOINT_SUCCESS){
                try {
                    int length = photos.length();
                    for(int i=0;i<length;i++){
                        TextView title = new TextView(getContext());
                        title.setText(photos.getJSONObject(i).getString("title"));
                        ImageView photo = new ImageView(getContext());
                        Picasso.with(getContext()).load(Constants.HISTORICAL_EVENTS_DIRECTORY+historicalEventId+"/photos/"+photos.getJSONObject(i).getString("file_name")).into(photo);
                        photosContainer.addView(title);
                        photosContainer.addView(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
