package com.example.hanzi_grouper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class OverviewRecyclerAdapter extends RecyclerView.Adapter<OverviewRecyclerAdapter.OverviewRecyclerHolder> {
    private ArrayList<Group> groups;
    private View.OnClickListener onClickListener;

    OverviewRecyclerAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @NonNull
    @Override
    public OverviewRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_overview_recycler, parent, false);
        OverviewRecyclerHolder holder = new OverviewRecyclerHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OverviewRecyclerHolder overviewRecyclerHolder, int position) {
        Group group = groups.get(position);
        ArrayList<String> characters = group.getCharacters();

        String nameText = group.getName().concat(": ");
        StringBuilder charactersText = new StringBuilder();

        for (int i = 0; i < group.size(); i++) {
            charactersText.append(" ").append(characters.get(i));
            if (i < group.size() - 1) {
                charactersText.append(",");
            }
        }

        overviewRecyclerHolder.groupName.setText(nameText);
        overviewRecyclerHolder.groupChars.setText(charactersText);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class OverviewRecyclerHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView groupChars;

        public OverviewRecyclerHolder(View v) {
            super(v);

            groupName = (TextView) v.findViewById(R.id.group_name);
            groupChars = (TextView) v.findViewById(R.id.group_chars);

            v.setTag(this);
            v.setOnClickListener(onClickListener);
        }
    }
}