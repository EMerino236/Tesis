package com.app.tesis.eduardo.tesis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Eduardo on 28/09/2016.
 */

public class PrePhotoItemFragment extends Fragment {
    TextView prephoto_title;
    ImageView prephoto;
    String userId;
    String prephotoTitle;
    String prephotoUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_item_prephoto, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        prephoto_title = (TextView)getView().findViewById(R.id.prephoto_title);
        prephoto = (ImageView)getView().findViewById(R.id.prephoto);
        userId = getArguments().getString("userId");
        prephotoTitle = getArguments().getString("prephotoTitle");
        prephotoUrl = getArguments().getString("prephotoUrl");
        prephoto_title.setText(prephotoTitle);
        Picasso.with(getContext()).load(prephotoUrl).into(prephoto);
        /*
        historical_event_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historical_event = new Intent(getActivity(), HistoricalEventActivity.class);
                historical_event.putExtra("historicalEventId",historicalEventId);
                historical_event.putExtra("userId",Integer.valueOf(userId));
                startActivity(historical_event);
            }
        });
        */
    }
}
