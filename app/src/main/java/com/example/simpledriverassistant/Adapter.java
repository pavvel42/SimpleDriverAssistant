package com.example.simpledriverassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Report> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mReportAction;
        public View mDeleteReport;
        public TextView reportAction, reportLatitude, reportLongitude;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mReportAction = itemView.findViewById(R.id.report_action);
            mDeleteReport = itemView.findViewById(R.id.report_delete);
            reportAction = itemView.findViewById(R.id.report_latitude);
            reportLatitude = itemView.findViewById(R.id.report_longitude);
            reportLongitude = itemView.findViewById(R.id.report_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public Adapter(ArrayList<Report> list) {
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_reports, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report currentItem = mList.get(position);

        holder.mReportAction.setImageResource(currentItem.getmImageResource());
        holder.reportAction.setText(currentItem.getAction());
        holder.reportLatitude.setText(currentItem.getLatitude()+"");
        holder.reportLongitude.setText(currentItem.getLongitude()+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
