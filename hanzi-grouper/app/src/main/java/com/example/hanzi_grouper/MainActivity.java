package com.example.hanzi_grouper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

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
//        ArrayList<String> group1 = new ArrayList<>();
//        group1.add("g1");
//        group1.add("滴");
//        group1.add("地");
//        group1.add("第");
//        ArrayList<String> group2 = new ArrayList<>();
//        group2.add("g2");
//        group2.add("值");
//        group2.add("只");
//        ArrayList<ArrayList<String>> groups = new ArrayList<>();
//        groups.add(group1);
//        groups.add(group2);


//        GroupPreferences.saveGroups(groups, this);

        ArrayList<ArrayList<String>> groups = GroupPreferences.loadGroups(this);

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

