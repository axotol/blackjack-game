package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class History extends AppCompatActivity {
    private HistoryListOpenHelper mDb;
    private TextView mTvDate;
    private TextView mTvTime;
    private TextView mTvTotalPlayed;
    private TextView mTvTotalWon;
    private TextView mTvWinningPercent;
    HistoryItem entry = new HistoryItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_history);

        mTvDate = findViewById(R.id.tvDate);
        mTvTime = findViewById(R.id.tvTime);
        mTvTotalPlayed = findViewById(R.id.tvTotalPlayed);
        mTvTotalWon = findViewById(R.id.tvTotalWon);
        mTvWinningPercent = findViewById(R.id.tvWinningPercent);

        //FOR TESTING PURPOSE WITHOUT DATABASE
        setContentView(R.layout.history_list_item);
        entry = mDb.displayRecord(1);

        mTvDate.setText(entry.getDate().toString());
        mTvTime.setText(entry.getTime().toString());
        mTvTotalPlayed.setText(entry.getTotalRoundPlayed().toString());
        mTvTotalWon.setText(entry.getTotalRoundWon().toString());
        mTvWinningPercent.setText(((int) entry.getWinningPercentage()));

    }
}
