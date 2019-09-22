package com.example.hanzi_grouper;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dictionary {
    private static Dictionary singleton;

    private HashMap<String, String> dictionary;
    private HashMap<String, String> simplifiedToPinyin = new HashMap<String, String>();
    private HashMap<String, String> simplifiedToMeaning = new HashMap<String, String>();

    // Data of each column gets loaded into its own ArrayList.
    private ArrayList<String> simplified = new ArrayList<>();
    private ArrayList<String> traditional = new ArrayList<>();
    private ArrayList<String> pinyin = new ArrayList<>();
    private ArrayList<String> meaningsUnsplit = new ArrayList<>();
    private ArrayList<ArrayList<String>> meanings = new ArrayList<>();

    private Dictionary(InputStream dictionaryStream) {
        parseDictionaryFile(dictionaryStream);
    }

    public static Dictionary getDictionary(InputStream dictionaryStream) {
        if (singleton == null) {
            Log.d("custom", "Parsing dictionary file.");
            singleton = new Dictionary(dictionaryStream);
            Log.d("custom", "Finished parsing dictionary file.");
        }
        else {
            Log.d("custom", "Returning previously loaded dictionary.");
        }

        return singleton;
    }

    private void parseDictionaryFile(InputStream dictionaryStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(dictionaryStream));

        try {
//            dictionary = new Hashtable<>();
            String line;
            String[] delimited = new String[2];

            while (reader.ready()) {
                line = reader.readLine();

                // Discard comments.
                if (!line.startsWith("#")) {
                    delimited = line.split(" ", 2);

                    // Only include single characters.
                    if (delimited[0].length() == 1) {
                        traditional.add(delimited[0]);

                        delimited = delimited[1].split(" \\[", 2);
                        simplified.add(delimited[0]);

                        delimited = delimited[1].split("\\] \\/", 2);
                        pinyin.add(delimited[0].replace("u:", "ü"));

                        // Remove trailing forward slash.
                        delimited[1] = delimited[1].substring(0, delimited[1].length() - 1);
                        meaningsUnsplit.add(delimited[1]);

                        delimited = delimited[1].split("\\/");
                        meanings.add(new ArrayList<String>(Arrays.asList(delimited)));
                    }
                }
            }

            reader.close();

            for (int i = 0; i < simplified.size(); i++) {
                if (simplified.get(i) == null ||
                        pinyin.get(i) == null ||
                        meaningsUnsplit.get(i) == null) {
                    Log.d("custom", simplified.get(i));
                    Log.d("custom", pinyin.get(i));
                    Log.d("custom", meaningsUnsplit.get(i));
                }
                else {
                    simplifiedToPinyin.put(simplified.get(i), pinyin.get(i));
                    simplifiedToMeaning.put(simplified.get(i), meaningsUnsplit.get(i));
                }
            }
        } catch (IOException e) {
            // TODO: catch or throw exception
            Log.d("custom", "Couldn't parse dictionary resource.");
        }
    }

    // TODO: implement
    // TODO: multiple results
    public String[] search(String search) {
        // index 0: hanzi
        // index 1: pinyin
        // index 2: meaning
        String[] record = new String[3];

        // search for character or pinyin

        // if record can be found
            // store record in arraylist
            return record;
        // else
            // return null;
    }

    public ArrayList<String> findEntryByCharacter(String search) {
        ArrayList<String> result = new ArrayList<String>();

        if (singleton == null) {
            return null;    // return null if dictionary is not loaded yet
        }

        int index = simplified.indexOf(search);

        if (index != -1) {
            result.add(simplified.get(index));
            result.add(pinyin.get(index));
            result.add(meaningsUnsplit.get(index));

            return result;
        }

        return null;    // return null if character is not found
    }

    public ArrayList<ArrayList<String>> findEntryByPinyin(String search) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(new ArrayList<String>());    // characters
        result.add(new ArrayList<String>());    // pinyin
        result.add(new ArrayList<String>());    // meanings

        if (singleton == null) {
            return null;    // return null if dictionary is not loaded yet
        }

        for (int i = 0; i < simplified.size(); i++) {
            if (simplified.get(i) != null && simplified.get(i).contains(search)) {
                result.get(0).add(simplified.get(i));
                result.get(1).add(pinyin.get(i));
                result.get(2).add(meaningsUnsplit.get(i));
            }
        }

        if (!result.get(0).isEmpty()) {
            return result;
        }
        else {
            return null;    // return null if pinyin is not found
        }
    }
}
