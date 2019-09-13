package com.example.hanzi_grouper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        RecyclerView overviewRecycler = (RecyclerView) findViewById(R.id.overview_recycler);

        ArrayList<ArrayList<String>> groups = loadGroups();

        RecyclerView.Adapter overviewRecyclerAdapter = new OverviewRecyclerAdapter(groups);
        overviewRecycler.setAdapter(overviewRecyclerAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        overviewRecycler.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ArrayList<String>> loadGroups() {
        // dummy data
        ArrayList<String> group1 = new ArrayList<>();
        group1.add("g1");
        group1.add("滴");
        group1.add("地");
        group1.add("第");
        ArrayList<String> group2 = new ArrayList<>();
        group2.add("g2");
        group2.add("值");
        group2.add("只");
        ArrayList<ArrayList<String>> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        return groups;
    }
}

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