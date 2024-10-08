package com.example.fetch_interview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {
    final List<DataHolder> mData;
    private final LayoutInflater mInflater;

    // data is passed into the constructor
    ListRecyclerViewAdapter(Context context, List<DataHolder> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataHolder data = mData.get(position);

        if (data.isRowHeader()) {
            holder.myNameColumn.setVisibility(View.GONE);
            holder.myRowIdColumn.setVisibility(View.GONE);
            holder.myIdColumn.setVisibility(View.GONE);
            holder.myHeader.setVisibility(View.VISIBLE);
            holder.myHeader.setText(data.getListId());
        }
        else {
            holder.myNameColumn.setText(data.getPopulatedName());
            holder.myIdColumn.setText(data.getId());
            holder.myRowIdColumn.setText(data.getListId());
            holder.myNameColumn.setBackgroundResource(android.R.color.darker_gray);
            holder.myIdColumn.setBackgroundResource(android.R.color.darker_gray);
            holder.myRowIdColumn.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void onRefresh() {
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myNameColumn;
        TextView myIdColumn;
        TextView myRowIdColumn;
        TextView myHeader;

        ViewHolder(View itemView) {
            super(itemView);
            myNameColumn = itemView.findViewById(R.id.name_column);
            myIdColumn = itemView.findViewById(R.id.id_column);
            myRowIdColumn = itemView.findViewById(R.id.list_id_column);
            myHeader = itemView.findViewById(R.id.id_label);
        }
    }

}
