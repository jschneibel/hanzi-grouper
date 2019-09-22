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
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    static final String EXTRA_CHARACTER = "com.example.hanzi_grouper.CHARACTER";

    private Dictionary dictionary;      // static singleton
    private ArrayList<Group> groups;    // all persisted groups
    private Group group;                // displayed group

    private GroupRecyclerAdapter groupRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groups = GroupPreferences.loadGroups(this);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra(MainActivity.EXTRA_GROUP);
        group = GroupPreferences.findGroupByName(groups, groupName);

        getSupportActionBar().setTitle(group.getName());

        FloatingActionButton fab = findViewById(R.id.new_character);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> characters = group.getCharacters();
                ArrayList<String> pinyin = group.getPinyin();
                ArrayList<String> meanings = group.getMeanings();

                ArrayList<String> result = dictionary.findEntryByCharacter("绿");
//                绿椋
                characters.add(result.get(0));
                pinyin.add(result.get(1));
                meanings.add(result.get(2));
                group.setEntries(characters, pinyin, meanings);

                groupRecyclerAdapter.notifyItemInserted(group.size() - 1);
                GroupPreferences.saveGroups(groups, GroupActivity.this);

                Snackbar.make(view, "Add sample character.", Snackbar.LENGTH_LONG)
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
        groupRecyclerAdapter = new GroupRecyclerAdapter(group, toneColors);
        groupRecycler.setAdapter(groupRecyclerAdapter);
        groupRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerAdapter.setOnClickListener(new GroupRecyclerOnClickListener());
    }

    class GroupRecyclerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            String character = group.getCharacters().get(position);

            Intent intent = new Intent(GroupActivity.this, CharacterActivity.class);
            intent.putExtra(MainActivity.EXTRA_GROUP, group.getName());
            intent.putExtra(EXTRA_CHARACTER, character);
            startActivity(intent);

//            Snackbar.make(findViewById(R.id.new_character), "Character: " + character, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        }
    }
}
