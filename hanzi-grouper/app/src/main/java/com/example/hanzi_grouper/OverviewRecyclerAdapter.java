package com.example.hanzi_grouper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class OverviewRecyclerAdapter extends RecyclerView.Adapter<OverviewRecyclerAdapter.OverviewRecyclerHolder> {
    ArrayList<ArrayList<String>> groups;

    class OverviewRecyclerHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView groupChars;

        public OverviewRecyclerHolder(View v) {
            super(v);

            groupName = (TextView) v.findViewById(R.id.group_name);
            groupChars = (TextView) v.findViewById(R.id.group_chars);
        }
    }

    OverviewRecyclerAdapter(ArrayList<ArrayList<String>> groups) {
        this.groups = groups;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @NonNull
    @Override
    public OverviewRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_recycler_layout, parent, false);
        OverviewRecyclerHolder holder = new OverviewRecyclerHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OverviewRecyclerHolder overviewRecyclerHolder, int position) {
        ArrayList<String> group = groups.get(position);

        String name = group.get(0).concat(": ");
        String chars = "";

        for (int i = 1; i < group.size(); i++) {
            chars = chars.concat(" ").concat(group.get(i));
        }

        overviewRecyclerHolder.groupName.setText(name);
        overviewRecyclerHolder.groupChars.setText(chars);
    }
}