package com.example.hanzi_grouper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.InputStream;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    
    private Dictionary dictionary;          // static singleton
    private Decompositions decompositions;  // static singleton
    private ArrayList<Group> groups;        // all persisted groups
    private Group group;                    // displayed group

    private GroupRecyclerAdapter groupRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groups = GroupPreferences.loadGroups(this);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);
        InputStream decompositionsStream = getResources().openRawResource(R.raw.decompositions);
        decompositions = Decompositions.getDecompositions(decompositionsStream);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra(Extras.EXTRA_GROUP);
        group = GroupPreferences.findGroupByName(groups, groupName);

        getSupportActionBar().setTitle(groupName);

        FloatingActionButton fab = findViewById(R.id.new_character);
        fab.setOnClickListener(new NewCharacterOnClickListener());

        int[] toneColors = {
                ContextCompat.getColor(this, R.color.colorTone1),
                ContextCompat.getColor(this, R.color.colorTone2),
                ContextCompat.getColor(this, R.color.colorTone3),
                ContextCompat.getColor(this, R.color.colorTone4),
                ContextCompat.getColor(this, R.color.colorTone5)
        };

        RecyclerView groupRecycler = (RecyclerView) findViewById(R.id.group_recycler);
        groupRecyclerAdapter = new GroupRecyclerAdapter(group, toneColors);
        groupRecycler.setAdapter(groupRecyclerAdapter);
        groupRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerAdapter.setOnClickListener(new GroupRecyclerOnClickListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
            LayoutInflater inflater = getLayoutInflater();

            builder.setTitle("Delete Group")
                    .setMessage("Delete group '" + group.getName() + "'?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            groups.remove(group);
                            GroupPreferences.saveGroups(groups, GroupActivity.this);

                            String snackbarMessage = "Group '" + group.getName() + "' deleted.";

                            Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                            intent.putExtra(Extras.EXTRA_MESSAGE, snackbarMessage);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            builder.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    class NewCharacterOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_new_character, null);
            final EditText characterEditText = (EditText) dialogView.findViewById(R.id.new_character);

            builder.setView(dialogView)
                    .setTitle("Add Character to Group")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String newCharacter = characterEditText.getText().toString();

                            String snackbarMessage;

                            ArrayList<String> characters = group.getCharacters();
                            ArrayList<String> pinyin = group.getPinyin();
                            ArrayList<String> meanings = group.getMeanings();

                            // Sample characters: 绿 椋 绹 咚 锵 恭 喜
                            ArrayList<String> result = dictionary.findEntryByCharacter(newCharacter);
                            if (result != null) {
                                characters.add(result.get(0));
                                pinyin.add(result.get(1));
                                meanings.add(result.get(2));
                                group.setEntries(characters, pinyin, meanings);

                                groupRecyclerAdapter.notifyItemInserted(group.size() - 1);

                                GroupPreferences.saveGroups(groups, GroupActivity.this);

                                snackbarMessage = "Character " + newCharacter + " added.";
                            }
                            else {
                                snackbarMessage = "Character " + newCharacter + " not found.";
                            }

                            Snackbar.make(findViewById(R.id.new_character), snackbarMessage, Snackbar.LENGTH_LONG)
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
            characterEditText.requestFocus();
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

    class GroupRecyclerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            String character = group.getCharacters().get(position);

            Intent intent = new Intent(GroupActivity.this, CharacterActivity.class);
            intent.putExtra(Extras.EXTRA_GROUP, group.getName());
            intent.putExtra(Extras.EXTRA_GROUP_CHARACTER, character);
            startActivity(intent);

//            Snackbar.make(findViewById(R.id.new_character), "Character: " + character, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        }
    }
}
