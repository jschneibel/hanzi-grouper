package com.example.hanzi_grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class CharacterActivity extends AppCompatActivity {

    static final String EXTRA_SIMILAR_CHARACTER = "com.example.hanzi_grouper.SIMILAR_CHARACTER";

    private Dictionary dictionary;                  // static singleton
    private Decompositions decompositions;          // static singleton
    private ArrayList<Group> groups;                // all persisted groups
    private Group group;                            // displayed group
    private String groupName;                       // selected group
    private String groupCharacter;                  // selected character of selected group
    private String similarCharacter;                // selected similar character (may be null)
    private ArrayList<String> recommendedCharacters;    // recommended similar characters
    // displayed character (groupCharacter or similarCharacter)
    private ArrayList<String> displayedCharacter = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groups = GroupPreferences.loadGroups(this);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);
        InputStream decompositionsStream = getResources().openRawResource(R.raw.decompositions);
        decompositions = Decompositions.getDecompositions(decompositionsStream);

        Intent intent = getIntent();
        groupName = intent.getStringExtra(MainActivity.EXTRA_GROUP);
        groupCharacter = intent.getStringExtra(GroupActivity.EXTRA_GROUP_CHARACTER);
        similarCharacter = intent.getStringExtra(CharacterActivity.EXTRA_SIMILAR_CHARACTER);

        if (similarCharacter == null) {
            displayedCharacter.add(groupCharacter);

            // up action: go back to selected group
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
                    intent.putExtra(MainActivity.EXTRA_GROUP, groupName);
                    startActivity(intent);
                }
            });

            group = GroupPreferences.findGroupByName(groups, groupName);
            int characterIndex = group.getCharacters().indexOf(groupCharacter);

            displayedCharacter.add(group
                    .getPinyin()
                    .get(characterIndex));
            displayedCharacter.add(group
                    .getMeanings()
                    .get(characterIndex)
                    .replace("/", "\n"));

            getSupportActionBar().setTitle(groupName + ": " + groupCharacter);
        } else {
            displayedCharacter.add(similarCharacter);

            // up action: go back to selected character of selected group
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CharacterActivity.this, CharacterActivity.class);
                    intent.putExtra(MainActivity.EXTRA_GROUP, groupName);
                    intent.putExtra(GroupActivity.EXTRA_GROUP_CHARACTER, groupCharacter);
                    startActivity(intent);
                }
            });

            displayedCharacter = dictionary.findEntryByCharacter(displayedCharacter.get(0));

            getSupportActionBar().setTitle(groupName + ": " + groupCharacter + " → " + displayedCharacter.get(0) + "");
        }

        TextView characterView = (TextView) findViewById(R.id.character);
        TextView pinyinView = (TextView) findViewById(R.id.pinyin);
        TextView meaningsView = (TextView) findViewById(R.id.meanings);

        characterView.setText(displayedCharacter.get(0));
        pinyinView.setText(displayedCharacter.get(1));
        meaningsView.setText(displayedCharacter.get(2));

        int[] toneColors = {
                ContextCompat.getColor(this, R.color.colorTone1),
                ContextCompat.getColor(this, R.color.colorTone2),
                ContextCompat.getColor(this, R.color.colorTone3),
                ContextCompat.getColor(this, R.color.colorTone4),
                ContextCompat.getColor(this, R.color.colorTone5)
        };

        // set color of pinyin according to tone 1, 2, 3, 4 or 5 (= no tone)
        for (int i = 1; i <= 5; i++) {
            if (displayedCharacter.get(1).contains(Integer.toString(i))) {
                pinyinView.setTextColor(toneColors[i - 1]);
                break;
            }
        }

        recommendedCharacters = decompositions.findSimilarCharacters(displayedCharacter.get(0));

        LayoutInflater layoutInflater = getLayoutInflater();
        LinearLayout listLayout = (LinearLayout) findViewById(R.id.layout_similar_characters);
        for (final String recommendedCharacter : recommendedCharacters) {
            ArrayList<String> item = dictionary.findEntryByCharacter(recommendedCharacter);

            if (item != null) {
                LinearLayout itemLayout = new LinearLayout(this);
                listLayout.addView(itemLayout);
                View v = layoutInflater.inflate(R.layout.layout_group_recycler, itemLayout, true);
                TextView similarCharacterView = v.findViewById(R.id.character);
                TextView similarPinyinView = v.findViewById(R.id.pinyin);
                TextView similarMeaningsView = v.findViewById(R.id.meaning);

                similarCharacterView.setText(item.get(0));
                similarPinyinView.setText(item.get(1));
                similarMeaningsView.setText(item.get(2));

                for (int i = 1; i <= 5; i++) {
                    if (item.get(1).contains(Integer.toString(i))) {
                        similarPinyinView.setTextColor(toneColors[i - 1]);
                        break;
                    }
                }

                itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CharacterActivity.this, CharacterActivity.class);
                        intent.putExtra(MainActivity.EXTRA_GROUP, groupName);
                        intent.putExtra(GroupActivity.EXTRA_GROUP_CHARACTER, groupCharacter);
                        intent.putExtra(CharacterActivity.EXTRA_SIMILAR_CHARACTER, recommendedCharacter);
                        startActivity(intent);
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_character, menu);
        return true;
    }
}
