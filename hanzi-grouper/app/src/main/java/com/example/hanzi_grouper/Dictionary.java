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
                        simplified.add(delimited[0]);

                        delimited = delimited[1].split(" \\[", 2);
                        traditional.add(delimited[0]);

                        delimited = delimited[1].split("\\] \\/", 2);
                        pinyin.add(delimited[0]);

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

//            Log.d("custom", "Length simplified: " + simplified.size());
//            Log.d("custom", "Length traditional: " + traditional.size());
//            Log.d("custom", "Length pinyin: " + pinyin.size());
//            Log.d("custom", "Length meaningsUnsplit: " + meaningsUnsplit.size());
//            Log.d("custom", "Length meanings: " + meanings.size());

//            int id = 49982;
            int id = 5002;
            Log.d("custom", "Sample entry: ");
            Log.d("custom", simplified.get(id));
            Log.d("custom", traditional.get(id));
            Log.d("custom", pinyin.get(id));
            Log.d("custom", meaningsUnsplit.get(id));
            Log.d("custom", meanings.get(id).toString());

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

        // search for hanzi or pinyin

        // if record can be found
            // store record in arraylist
            return record;
        // else
            // return null;
    }

    public ArrayList<String> findEntryByCharacter(String character) {
        ArrayList<String> result = new ArrayList<String>();

        result.add("1");
        result.add("2");
        result.add("3");

        return result;
    }
}
