package com.example.hanzi_grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    static final String EXTRA_CHARACTER = "com.example.hanzi_grouper.CHARACTER";

    private ArrayList<String> group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        group = intent.getStringArrayListExtra(MainActivity.EXTRA_GROUP);

        getSupportActionBar().setTitle(group.get(0));

        FloatingActionButton fab = findViewById(R.id.new_character);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, group.get(0), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int[] toneColors = {
                ContextCompat.getColor(this, R.color.colorTone1),
                ContextCompat.getColor(this, R.color.colorTone2),
                ContextCompat.getColor(this, R.color.colorTone3),
                ContextCompat.getColor(this, R.color.colorTone4),
                ContextCompat.getColor(this, R.color.colorTone5)
        };

        RecyclerView groupRecycler = (RecyclerView) findViewById(R.id.group_recycler);
        GroupRecyclerAdapter groupRecyclerAdapter = new GroupRecyclerAdapter(group, toneColors);
        groupRecycler.setAdapter(groupRecyclerAdapter);
        groupRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerAdapter.setOnClickListener(new GroupActivity.GroupRecyclerOnClickListener());
    }

    class GroupRecyclerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            String character = group.get(position + 1);

//            Intent intent = new Intent(GroupActivity.this, CharacterActivity.class);
//            intent.putExtra(EXTRA_CHARACTER, group.get(position));
//            startActivity(intent);

            Snackbar.make(findViewById(R.id.new_character), "Character: " + character, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
