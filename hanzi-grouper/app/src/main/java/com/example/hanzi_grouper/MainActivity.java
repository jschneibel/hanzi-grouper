package com.example.hanzi_grouper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String EXTRA_GROUP = "com.example.hanzi_grouper.GROUP";

    private ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups = GroupPreferences.loadGroups(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.new_group);
        fab.setOnClickListener(new NewGroupOnClickListener());

        RecyclerView overviewRecycler = (RecyclerView) findViewById(R.id.overview_recycler);
        OverviewRecyclerAdapter overviewRecyclerAdapter = new OverviewRecyclerAdapter(groups);
        overviewRecycler.setAdapter(overviewRecyclerAdapter);
        overviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        overviewRecyclerAdapter.setOnClickListener(new OverviewRecyclerOnClickListener());
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

    class NewGroupOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_new_group, null);
            final EditText groupNameEditText = (EditText) dialogView.findViewById(R.id.new_group_name);

            builder.setView(dialogView)
                    .setTitle("Create New Group")
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String newGroupName = groupNameEditText.getText().toString();

                            Group group = GroupPreferences.findGroupByName(groups, newGroupName);
                            String snackbarMessage;

                            if (newGroupName.equals("")) {
                                snackbarMessage = "No group name entered.";
                            }
                            else if (group == null) {    // group is null if name is not a duplicate
                                group = new Group(newGroupName);
                                groups.add(group);
                                GroupPreferences.saveGroups(groups, MainActivity.this);
                                snackbarMessage = "New group '" + newGroupName + "' created.";
                            }
                            else {
                                snackbarMessage = "Group '" + newGroupName + "' already exists!";
                            }

                            closeKeyboard();
                            Snackbar.make(findViewById(R.id.new_group), snackbarMessage, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            closeKeyboard();
                        }
                    });

            builder.show();
            groupNameEditText.requestFocus();
            showKeyboard();
        }

        private void showKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        private void closeKeyboard(){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    class OverviewRecyclerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String groupName = groups.get(position).getName();

            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            intent.putExtra(EXTRA_GROUP, groups.get(position));
            startActivity(intent);

            Snackbar.make(findViewById(R.id.new_group), "nr: " + groupName, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}

