package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Eduardo on 9/09/2016.
 */
public class UploadFragment extends Fragment {
    private String historicalEventId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicalEventId = getArguments().getString("historicalEventId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_he_upload, container, false);
        return view;
    }
}
