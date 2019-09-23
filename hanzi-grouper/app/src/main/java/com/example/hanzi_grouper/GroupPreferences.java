package com.example.hanzi_grouper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Structure of SharedPreferences:
//
// Name of SharedPreference: PREFERENCES
//
// Key: GROUP_NAMES
// Value: group names delimited by DELIMITER
//
// Key: group name prefixed by GROUP_NAME_PREFIX
// Value: characters, pinyin and meanings (in this order per character) of the group
// delimited by DELIMITER
//
// If a group is deleted, its contents get "orphaned" (its key is deleted, its value is not).

public abstract class GroupPreferences {

    private static final String PREFERENCES = "groups"; // Name of the SharedPreference
    private static final String GROUP_NAMES = "groupNames"; // Key containing all group names
    private static final String GROUP_NAME_PREFIX = "#"; // Prefix for each group name key
    private static final String DELIMITER = Character.toString((char) 31); // ASCII 'unit separator'
    private static final String DELIMITER_REGEX = Character.toString((char) 31);

    public static void saveGroups(ArrayList<Group> groups, Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        StringBuilder groupNames = new StringBuilder();

        for (Group group : groups) {
            groupNames.append(group.getName());
            groupNames.append(DELIMITER);

            StringBuilder groupEntries = new StringBuilder();

            if (group.size() > 0) {

                for (int i = 0; i < group.size(); i++) {
                    groupEntries.append(group.getCharacters().get(i));
                    groupEntries.append(DELIMITER);
                    groupEntries.append(group.getPinyin().get(i));
                    groupEntries.append(DELIMITER);
                    groupEntries.append(group.getMeanings().get(i));
                    groupEntries.append(DELIMITER);
                }
            }

            editor.putString(GROUP_NAME_PREFIX.concat(group.getName()), groupEntries.toString());
        }

        editor.putString(GROUP_NAMES, groupNames.toString());

        editor.apply();
    }

    public static ArrayList<Group> loadGroups(Context ctx) {
        ArrayList<Group> groups = new ArrayList<>();

        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES, 0);

        String[] groupNames = preferences.getString(GROUP_NAMES, "").split(DELIMITER_REGEX);
        if (groupNames[0].equals("")) {
            return groups;  // return empty ArrayList if there are no groups
        }

        String[] groupContents;
        List<String> characters;
        List<String> pinyin;
        List<String> meanings;
        Group group;

        for (String groupName : groupNames) {
            if (groupName.equals("")) {
                continue;
            }
            group = new Group(groupName);
            characters = new ArrayList<>();
            pinyin = new ArrayList<>();
            meanings = new ArrayList<>();

            groupContents = preferences.
                    getString(GROUP_NAME_PREFIX.concat(groupName), "").
                    split(DELIMITER_REGEX); // is not null

            int i = 0;

            if (groupContents.length % 3 == 0) {
                while (i < groupContents.length) {
                    characters.add(groupContents[i]);
                    i++;
                    pinyin.add(groupContents[i]);
                    i++;
                    meanings.add(groupContents[i]);
                    i++;
                }
            } else if (groupContents[0].equals("")) {

            } else {
                Snackbar.make(((Activity) ctx).findViewById(R.id.toolbar), "Deleted corrupted group '" + groupName + "'.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                break; // omit group if data is corrupted
            }

            group.setEntries(characters, pinyin, meanings);

            groups.add(group);
        }

        return groups;
    }

    public static Group findGroupByName(ArrayList<Group> groups, String groupName) {
        for (Group group : groups) {
            if (group.getName().equals(groupName))
                return group;
        }

        return null;
    }
}