package com.example.hanzi_grouper;

import android.content.SharedPreferences;
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

        // dummmy group
        ArrayList<String> group;
        group = new ArrayList<String>();
        group.add("g3");
        group.add("a");
        group.add("b");
        group.add("c");

        SharedPreferences groupPreferences = getSharedPreferences("groups", 0);
        saveNewGroup(group, groupPreferences);
        ArrayList<ArrayList<String>> groups = loadGroups(groupPreferences);

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

    private ArrayList<ArrayList<String>> loadGroups(SharedPreferences groupPreferences) {
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

    private void saveNewGroup(ArrayList<String> group, SharedPreferences groupPreferences) {
        // dummmy group
        group = new ArrayList<String>();
        group.add("g3");
        group.add("a");
        group.add("b");
        group.add("c");

        groupPreferences = getSharedPreferences("groups", 0);
        SharedPreferences.Editor groupEditor = groupPreferences.edit();

        int groupIndex = 1;
        int groupSize = group.size() - 1;

        groupEditor.putString(
                "0000",
                "1");
        groupEditor.putString(
                parseGroupPreferences(new int[]{groupIndex, 0}),
                Integer.toString(groupSize));

        for (int i = 0; i < group.size(); i++) {
            groupEditor.putString(
                    parseGroupPreferences(new int[]{groupIndex, i + 1}),
                    group.get(i));
        }

        groupEditor.commit();
    }

    // TODO: save preferences with delimited strings instead?

    // group preferences format:
    // max 99 groups
    // max 99 characters per group
    // 0000: max group index (number of groups)
    // 0100: number of characters in first group
    // 0101: name of first group
    // 0102: first character of first group
    // 0103: second character of first group
    // 0412: 11th character of 4th group

    private String parseGroupPreferences(int[] index) {
        String groupIndex;
        String characterIndex;

        // parse groupIndex
        if (index[0] <= 9)
            groupIndex = "0".concat(Integer.toString(index[0]));
        else if (index[0] <= 99)
            groupIndex = Integer.toString(index[0]);
        else
            groupIndex = "00";


        // parse characterIndex
        if (index[1] > 0 && index[1] < 10)
            characterIndex = "0".concat(Integer.toString(index[1]));
        else if (index[1] < 100)
            characterIndex = Integer.toString(index[1]);
        else
            characterIndex = "10";

        return groupIndex + characterIndex;
    }

    private int[] parseGroupPreferences(String index) {
        int groupIndex;
        int characterIndex;

        groupIndex = Integer.parseInt(index.substring(0, 2));
        characterIndex = Integer.parseInt(index.substring(2, 4));

        return new int[]{groupIndex, characterIndex};
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