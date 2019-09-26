package com.example.hanzi_grouper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class CharacterActivity extends AppCompatActivity {

    private boolean showMenuItemAdd = false;

    private Dictionary dictionary;                  // static singleton
    private Decompositions decompositions;          // static singleton
    private ArrayList<Group> groups;                // all persisted groups
    private Group group;                            // displayed group
    private String groupName;                       // selected group
    private String groupCharacter;                  // selected character of selected group
    private String similarCharacter;                // selected similar character (may be null)
    private ArrayList<String> recommendedCharacters;    // recommended similar characters
    // displayed character (groupCharacter or similarCharacter) with character, pinyin and meaning
    private ArrayList<String> displayedCharacter = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputStream dictionaryStream = getResources().openRawResource(R.raw.cedict_ts);
        dictionary = Dictionary.getDictionary(dictionaryStream);
        InputStream decompositionsStream = getResources().openRawResource(R.raw.decompositions);
        decompositions = Decompositions.getDecompositions(decompositionsStream);

        Intent intent = getIntent();
        groupName = intent.getStringExtra(Extras.EXTRA_GROUP);
        groupCharacter = intent.getStringExtra(Extras.EXTRA_GROUP_CHARACTER);
        similarCharacter = intent.getStringExtra(Extras.EXTRA_SIMILAR_CHARACTER);

        groups = GroupPreferences.loadGroups(this);
        group = GroupPreferences.findGroupByName(groups, groupName);

        if (similarCharacter == null) {
            displayedCharacter.add(groupCharacter);

            // up action: go back to selected group
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
                    intent.putExtra(Extras.EXTRA_GROUP, groupName);
                    startActivity(intent);
                }
            });

            int characterIndex = group.getCharacters().indexOf(groupCharacter);

            displayedCharacter.add(group
                    .getPinyin()
                    .get(characterIndex));
            displayedCharacter.add(group
                    .getMeanings()
                    .get(characterIndex));

            getSupportActionBar().setTitle(groupName + ": " + groupCharacter);
            showMenuItemAdd = false;
            invalidateOptionsMenu();
        } else {
            displayedCharacter.add(similarCharacter);

            // up action: go back to selected character of selected group
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CharacterActivity.this, CharacterActivity.class);
                    intent.putExtra(Extras.EXTRA_GROUP, groupName);
                    intent.putExtra(Extras.EXTRA_GROUP_CHARACTER, groupCharacter);
                    startActivity(intent);
                }
            });

            displayedCharacter = dictionary.findEntryByCharacter(displayedCharacter.get(0));

            getSupportActionBar().setTitle(groupName + ": " + groupCharacter + " → " + displayedCharacter.get(0) + "");
            showMenuItemAdd = true;
            invalidateOptionsMenu();
        }

        TextView characterView = (TextView) findViewById(R.id.character);
        TextView pinyinView = (TextView) findViewById(R.id.pinyin);
        TextView meaningsView = (TextView) findViewById(R.id.meanings);

        characterView.setText(displayedCharacter.get(0));
        pinyinView.setText(displayedCharacter.get(1));
        meaningsView.setText(displayedCharacter.get(2).replace("/", "\n"));

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
                        intent.putExtra(Extras.EXTRA_GROUP, groupName);
                        intent.putExtra(Extras.EXTRA_GROUP_CHARACTER, groupCharacter);
                        intent.putExtra(Extras.EXTRA_SIMILAR_CHARACTER, recommendedCharacter);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CharacterActivity.this);

            builder.setTitle("Delete Character")
                    .setMessage("Delete character '" + groupCharacter + "' from group '" + group.getName() + "'?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            group.removeEntry(groupCharacter);
                            GroupPreferences.saveGroups(groups, CharacterActivity.this);

                            String snackbarMessage = "Character '" + groupCharacter + "' deleted.";

                            Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
                            intent.putExtra(Extras.EXTRA_MESSAGE, snackbarMessage);
                            intent.putExtra(Extras.EXTRA_GROUP, groupName);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            builder.show();

            return true;
        }

        if (id == R.id.add) {
            group.addEntry(displayedCharacter.get(0),
                    displayedCharacter.get(1),
                    displayedCharacter.get(2));
            GroupPreferences.saveGroups(groups, CharacterActivity.this);

            String snackbarMessage = "Character '" + displayedCharacter.get(0) + "' added.";

            Intent intent = new Intent(CharacterActivity.this, GroupActivity.class);
            intent.putExtra(Extras.EXTRA_MESSAGE, snackbarMessage);
            intent.putExtra(Extras.EXTRA_GROUP, groupName);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_character, menu);

        menu.findItem(R.id.add).setVisible(showMenuItemAdd);
        menu.findItem(R.id.delete).setVisible(!showMenuItemAdd);

        return true;
    }
}
