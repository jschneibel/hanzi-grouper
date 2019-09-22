package com.example.hanzi_grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class CharacterActivity extends AppCompatActivity {

    private Dictionary dictionary;      // static singleton
    private ArrayList<Group> groups;    // all persisted groups
    private Group group;                // displayed group
    private String groupName;
    private String character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // up action
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
                intent.putExtra(MainActivity.EXTRA_GROUP, groupName);
                startActivity(intent);
            }
        });

        groups = GroupPreferences.loadGroups(this);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);

        Intent intent = getIntent();
        groupName = intent.getStringExtra(MainActivity.EXTRA_GROUP);
        character = intent.getStringExtra(GroupActivity.EXTRA_CHARACTER);

        group = GroupPreferences.findGroupByName(groups, groupName);
        int characterIndex = group.getCharacters().indexOf(character);

        getSupportActionBar().setTitle(groupName + ": " + character);

        String pinyin = group.getPinyin().get(characterIndex);
        String meanings = group.getMeanings().get(characterIndex);
        meanings = meanings.replace("/", "\n");

        TextView characterView = (TextView) findViewById(R.id.character);
        TextView pinyinView = (TextView) findViewById(R.id.pinyin);
        TextView meaningsView = (TextView) findViewById(R.id.meanings);

        characterView.setText(character);
        pinyinView.setText(pinyin);
        meaningsView.setText(meanings);

        int[] toneColors = {
                ContextCompat.getColor(this, R.color.colorTone1),
                ContextCompat.getColor(this, R.color.colorTone2),
                ContextCompat.getColor(this, R.color.colorTone3),
                ContextCompat.getColor(this, R.color.colorTone4),
                ContextCompat.getColor(this, R.color.colorTone5)
        };

        for (int i = 1; i <= 5; i++) {
            if (pinyin.contains(Integer.toString(i))) {
                pinyinView.setTextColor(toneColors[i - 1]);
                break;
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

}
