package com.example.hanzi_grouper;

import android.app.Activity;
import android.content.DialogInterface;
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

    private ArrayList<ArrayList<String>> groups;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_new_group, null);
                final EditText groupNameEditText = (EditText) dialogView.findViewById(R.id.new_group_name);

                builder.setView(dialogView)
                        .setTitle("Create New Group")
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newGroupName = groupNameEditText.getText().toString();

                                ArrayList<String> group = GroupPreferences.findGroupByName(groups, newGroupName);
                                String snackbarMessage;

                                if (newGroupName.equals("")) {
                                    snackbarMessage = "No group name entered.";
                                }
                                else if (group == null) {    // group is null if name is not a duplicate
                                    group = new ArrayList<>();
                                    group.add(newGroupName);
                                    groups.add(group);
                                    GroupPreferences.saveGroups(groups, MainActivity.this);
                                    snackbarMessage = "New group '" + newGroupName + "' created.";
                                }
                                else {
                                    snackbarMessage = "Group '" + newGroupName + "' already exists!";
                                }

                                closeKeyboard();
                                Snackbar.make(findViewById(R.id.fab), snackbarMessage, Snackbar.LENGTH_LONG)
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
        });

        groups = GroupPreferences.loadGroups(this);

        RecyclerView overviewRecycler = (RecyclerView) findViewById(R.id.overview_recycler);
        OverviewRecyclerAdapter overviewRecyclerAdapter = new OverviewRecyclerAdapter(groups);
        overviewRecycler.setAdapter(overviewRecyclerAdapter);
        overviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        overviewRecyclerAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                String groupName = groups.get(position).get(0);

                Snackbar.make(findViewById(R.id.fab), "nr: " + groupName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}

