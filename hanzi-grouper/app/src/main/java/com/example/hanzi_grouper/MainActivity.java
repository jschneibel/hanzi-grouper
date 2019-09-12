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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
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

        // dummy data
        ArrayList<String> group1 = new ArrayList<>();
        group1.add("g1");
        group1.add("h1");
        group1.add("h2");
        group1.add("h3");
        ArrayList<String> group2 = new ArrayList<>();
        group2.add("g2");
        group2.add("h1");
        group2.add("h2");
        ArrayList<ArrayList<String>> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

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
}

class OverviewRecyclerHolder extends RecyclerView.ViewHolder {
    TextView group;

    public OverviewRecyclerHolder(View v) {
        super(v);

        group = (TextView) v.findViewById(R.id.group);
    }
}

class OverviewRecyclerAdapter extends RecyclerView.Adapter<OverviewRecyclerHolder> {
    ArrayList<ArrayList<String>> groups;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_layout, parent, false);
        OverviewRecyclerHolder holder = new OverviewRecyclerHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OverviewRecyclerHolder overviewRecyclerHolder, int position) {
        ArrayList<String> group = groups.get(position);

        Log.d("test", groups.toString());
        Log.d("test", group.toString());
        String output = group.get(0).concat(":");
        for (int i = 1; i < group.size(); i++) {
            output = output.concat(" ").concat(group.get(i));
        }
        overviewRecyclerHolder.group.setText(output);
    }
}