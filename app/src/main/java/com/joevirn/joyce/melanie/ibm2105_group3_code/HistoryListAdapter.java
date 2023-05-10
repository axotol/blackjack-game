package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>{
    private LinkedList<String> mHistoryList;
    private LayoutInflater mInflater;

    private static final String TAG = HistoryListAdapter.class.getSimpleName();
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    Context mContext;
    private HistoryListOpenHelper mDB;

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTvDate;
        public final TextView mTvTime;
        public final TextView mTvTotalPlayed;
        public final TextView mTvTotalWon;
        public final TextView mTvWinningPercent;
        final HistoryListAdapter mAdapter;

        HistoryViewHolder(View itemView, HistoryListAdapter adapter) {
            super(itemView);
            mTvDate = itemView.findViewById(R.id.tvDate);
            mTvTime = itemView.findViewById(R.id.tvTime);
            mTvTotalPlayed = itemView.findViewById(R.id.tvTotalPlayed);
            mTvTotalWon = itemView.findViewById(R.id.tvTotalWon);
            mTvWinningPercent = itemView.findViewById(R.id.tvWinningPercent);
            this.mAdapter = adapter;
        }
    }

    //storing data into adapter : LayoutInflater
    public HistoryListAdapter(Context context, HistoryListOpenHelper mDB) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mDB = mDB;
    }

    //storing data into adapter : Inflate history layout
    @NonNull
    @Override
    public HistoryListAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mItemView = mInflater.inflate(R.layout.history_list_item, parent, false);
        return new HistoryViewHolder(mItemView, this);
    }

    //storing data into adapter : connects data to the view holder
    @Override
    public void onBindViewHolder(@NonNull HistoryListAdapter.HistoryViewHolder viewHolder, int position) {
        HistoryItem current = mDB.query(position);
        viewHolder.mTvDate.setText(current.getDate());
        viewHolder.mTvTime.setText(current.getTime());
        viewHolder.mTvTotalPlayed.setText(current.getTotalRoundPlayed());
        viewHolder.mTvTotalWon.setText(current.getTotalRoundWon());
        viewHolder.mTvWinningPercent.setText(((int) current.getWinningPercentage()));
/*
        final HistoryViewHolder h = viewHolder;
        final int historyID = current.getHistoryID();
        final String userName = current.getHUserName();
  */
        /*
        String mCurrent = mHistoryList.get(position);
        viewHolder.mTvDate.setText(mCurrent);
        viewHolder.mTvTime.setText(mCurrent);
        viewHolder.mTvTotalPlayed.setText(mCurrent);
        viewHolder.mTvTotalWon.setText(mCurrent);
        viewHolder.mTvWinningPercent.setText(mCurrent);
        */
    }

    //storing data into adapter : LinkedList
    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }
}