package com.example.hanzi_grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;

public class CharacterActivity extends AppCompatActivity {

    private Dictionary dictionary;      // static singleton
    private String groupName;
    private String character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
                intent.putExtra(MainActivity.EXTRA_GROUP, groupName);
                startActivity(intent);
            }
        });

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);

        Intent intent = getIntent();
        groupName = intent.getStringExtra(MainActivity.EXTRA_GROUP);
        character = intent.getStringExtra(GroupActivity.EXTRA_CHARACTER);

        getSupportActionBar().setTitle(groupName + ": " + character);

        ((TextView) findViewById(R.id.character)).setText(character);

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
