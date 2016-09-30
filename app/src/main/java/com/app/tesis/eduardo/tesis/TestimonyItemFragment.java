package com.app.tesis.eduardo.tesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.GONE;

/**
 * Created by Eduardo on 27/09/2016.
 */

public class TestimonyItemFragment extends Fragment {
    LinearLayout historical_event_container;
    TextView date;
    TextView period_txt;
    TextView historical_event_title;
    String testimonyEventId;
    String userId;
    String day;
    String month;
    String year;
    String period;
    String testimonyTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_item_testimony, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        date = (TextView)getView().findViewById(R.id.date);
        period_txt = (TextView)getView().findViewById(R.id.period);
        historical_event_title = (TextView)getView().findViewById(R.id.historical_event_title);
        testimonyEventId = getArguments().getString("testimonyEventId");
        userId = getArguments().getString("userId");
        day = getArguments().getString("day");
        month = getArguments().getString("month");
        year = getArguments().getString("year");
        period = getArguments().getString("period");
        testimonyTitle = getArguments().getString("testimonyTitle");
        String dateBuilder = "";
        if (day.compareTo("null") != 0) {
            dateBuilder += day + "/";
        }
        if (month.compareTo("null") != 0) {
            dateBuilder += month + "/";
        }
        if (year.compareTo("null") != 0) {
            dateBuilder += year;
        }
        date.setText(dateBuilder);
        period_txt.setText(period);
        historical_event_title.setText(testimonyTitle);
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
