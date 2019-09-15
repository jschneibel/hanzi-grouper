package com.example.hanzi_grouper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public abstract class GroupPreferences {
    private static final String PREFERENCES = "groups";
    private static final String GROUP_NAMES = "groupNames";
    private static final String GROUP_NAME_PREFIX = "#"; // To avoid clashes with other preferences.
    private static final String DELIMITER = "|";
    private static final String DELIMITER_REGEX = "\\|";

    public static void saveGroups(ArrayList<ArrayList<String>> groups, Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        StringBuilder groupNames = new StringBuilder();
        StringBuilder groupCharacters;

        for (ArrayList<String> group : groups) {
            groupNames.append(group.get(0));
            groupNames.append(DELIMITER);

            groupCharacters = new StringBuilder();
            if (group.size() > 1) {
                for (int i = 1; i < group.size(); i++) {
                    groupCharacters.append(group.get(i));
                    groupCharacters.append(DELIMITER);
                }
            }
            editor.putString(GROUP_NAME_PREFIX.concat(group.get(0)), groupCharacters.toString());
        }

        editor.putString(GROUP_NAMES, groupNames.toString());

        editor.apply();
    }

    public static ArrayList<ArrayList<String>> loadGroups(Context ctx) {
        ArrayList<ArrayList<String>> groups = new ArrayList<>();

        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES, 0);

        String[] groupNames = preferences.getString(GROUP_NAMES, "").split(DELIMITER_REGEX);
        String[] groupCharacters;
        ArrayList<String> group;

        for (String groupName : groupNames) {
            group = new ArrayList<>();

            groupCharacters = preferences.
                    getString(GROUP_NAME_PREFIX.concat(groupName), "").
                    split(DELIMITER_REGEX);

            group.add(groupName);

            for (String character : groupCharacters)
                group.add(character);

            groups.add(group);
        }

        return groups;
    }

    public static ArrayList<String> findGroupByName(ArrayList<ArrayList<String>> groups, String groupName) {
        for (ArrayList<String> group : groups) {
            if (group.get(0).equals(groupName))
                return group;
        }

        return null;
    }
}
