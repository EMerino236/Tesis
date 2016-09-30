package com.app.tesis.eduardo.tesis;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

/**
 * Created by Eduardo on 27/09/2016.
 */

public class PreAudioFragment extends Fragment {

    private Integer userId;
    LinearLayout preaudiosContainer;
    int audiosLength;
    MediaPlayer mPlayerList[];
    Button playButtonList[];
    // Webservices
    private AccessServiceAPI m_ServiceAccess;
    JSONArray preaudios;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getInt("userId");
        m_ServiceAccess = new AccessServiceAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mc_preaudio, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        preaudiosContainer = (LinearLayout) getView().findViewById(R.id.preaudios_container);
        new TaskMCPreaudios().execute(String.valueOf(userId));
    }

    public class TaskMCPreaudios extends AsyncTask<String, Void, Integer> {
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
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringFromUrl_GET(Constants.ENDPOINT_URL+Constants.CITIZENS+params[0]+"/pre-audios"));
                if(jObjResult.getBoolean("error")){
                    return Constants.ENDPOINT_ERROR;
                }
                preaudios = jObjResult.getJSONArray("pre_audios");
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
                    audiosLength = preaudios.length();
                    mPlayerList = new MediaPlayer[audiosLength];
                    playButtonList = new Button[audiosLength];
                    if(audiosLength>0) {
                        for (int i = 0; i < audiosLength; i++) {
                            final int finalI = i;
                            TextView title = new TextView(getContext());
                            title.setText(preaudios.getJSONObject(i).getString("title"));
                            mPlayerList[i] = new MediaPlayer();
                            mPlayerList[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mPlayerList[i].setDataSource(Constants.HISTORICAL_EVENTS_DIRECTORY + preaudios.getJSONObject(i).getString("historical_event_id") + "/preAudios/" + preaudios.getJSONObject(i).getString("file_name"));
                            mPlayerList[i].prepare();
                            playButtonList[i] = new Button(getContext());
                            //Button playButton = new Button(getContext());
                            playButtonList[i].setText(R.string.button_play);
                            playButtonList[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // If the selected audio is playing
                                    if (mPlayerList[finalI] != null && mPlayerList[finalI].isPlaying()) {
                                        mPlayerList[finalI].pause();
                                        playButtonList[finalI].setText(R.string.button_play);
                                    } else {
                                        for (int j = 0; j < audiosLength; j++) {
                                            if (mPlayerList[j] != null && mPlayerList[j].isPlaying()) {
                                                mPlayerList[j].pause();
                                            }
                                            if (playButtonList[j] != null) {
                                                playButtonList[j].setText(R.string.button_play);
                                            }
                                        }
                                        mPlayerList[finalI].start();
                                        playButtonList[finalI].setText(R.string.button_playing);
                                    }
                                }
                            });
                            mPlayerList[i].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    playButtonList[finalI].setText(R.string.button_play);
                                }
                            });
                            preaudiosContainer.addView(title);
                            preaudiosContainer.addView(playButtonList[i]);
                        }
                    }else{
                        TextView emptyResults = new TextView(getContext());
                        emptyResults.setText(R.string.empty_result);
                        emptyResults.setGravity(Gravity.CENTER);
                        preaudiosContainer.addView(emptyResults);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
