package com.example.hanzi_grouper;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupRecyclerHolder> {
    private Group group;
    private int[] toneColors;
    private View.OnClickListener onClickListener;

    GroupRecyclerAdapter(Group group, int[] toneColors) {
        this.group = group;
        this.toneColors = toneColors;
    }

    @Override
    public int getItemCount() {
        return group.size() - 1;
    }

    @NonNull
    @Override
    public GroupRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_recycler, parent, false);
        return new GroupRecyclerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerHolder overviewRecyclerHolder, int position) {
        String character = group.getCharacters().get(position);
        String pinyin = "yan5";
        String meaning = "meaning is a meaning";

        overviewRecyclerHolder.characterView.setText(character);
        overviewRecyclerHolder.pinyinView.setText(pinyin);
        overviewRecyclerHolder.meaningView.setText(meaning);

        for (int i = 1; i <= 5; i++) {
            if (pinyin.contains(Integer.toString(i))) {
                overviewRecyclerHolder.pinyinView.setTextColor(toneColors[i - 1]);
                break;
            }
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class GroupRecyclerHolder extends RecyclerView.ViewHolder {
        TextView characterView;
        TextView pinyinView;
        TextView meaningView;

        public GroupRecyclerHolder(View v) {
            super(v);

            characterView = (TextView) v.findViewById(R.id.character);
            pinyinView = (TextView) v.findViewById(R.id.pinyin);
            meaningView = (TextView) v.findViewById(R.id.meaning);

            v.setTag(this);
            v.setOnClickListener(onClickListener);
        }
    }
}