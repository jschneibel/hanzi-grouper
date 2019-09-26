package com.example.hanzi_grouper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    private Dictionary dictionary;          // static singleton
    private Decompositions decompositions;  // static singleton
    private ArrayList<Group> groups;        // all persisted groups

    private OverviewRecyclerAdapter overviewRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groups = GroupPreferences.loadGroups(this);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);
        InputStream decompositionsStream = getResources().openRawResource(R.raw.decompositions);
        decompositions = Decompositions.getDecompositions(decompositionsStream);

        FloatingActionButton fab = findViewById(R.id.new_group);
        fab.setOnClickListener(new NewGroupOnClickListener());

        RecyclerView overviewRecycler = (RecyclerView) findViewById(R.id.overview_recycler);
        overviewRecyclerAdapter = new OverviewRecyclerAdapter(groups);
        overviewRecycler.setAdapter(overviewRecyclerAdapter);
        overviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        overviewRecyclerAdapter.setOnClickListener(new OverviewRecyclerOnClickListener());

        // show snackbar message if there is one (delivered by intent)
        Intent intent = getIntent();
        String snackbarMessage = intent.getStringExtra(Extras.EXTRA_MESSAGE);
        if (snackbarMessage != null) {
            Snackbar.make(overviewRecycler, snackbarMessage, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

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
                            } else if (group == null) {    // group is null if name is not a duplicate
                                group = new Group(newGroupName);
                                groups.add(group);

                                overviewRecyclerAdapter.notifyItemInserted(groups.size() - 1);

                                GroupPreferences.saveGroups(groups, MainActivity.this);

                                snackbarMessage = "New group '" + newGroupName + "' created.";
                            } else {
                                snackbarMessage = "Group '" + newGroupName + "' already exists.";
                            }

                            Snackbar.make(findViewById(R.id.new_group), snackbarMessage, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            closeKeyboard();
                        }
                    });

            builder.show();
            groupNameEditText.requestFocus();
            showKeyboard();
        }

        private void showKeyboard() {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        private void closeKeyboard() {
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
            intent.putExtra(Extras.EXTRA_GROUP, groupName);
            startActivity(intent);
        }
    }
}

