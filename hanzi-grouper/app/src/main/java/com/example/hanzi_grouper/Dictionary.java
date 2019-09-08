package com.example.hanzi_grouper;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class Dictionary {
    private static Dictionary singleton;

    private Hashtable<String, String> dictionary;
    private ArrayList<String> simplified;
    private ArrayList<String> traditional;
    private ArrayList<String> pinyin;
    private ArrayList<String> meaningsUnsplit;
    private ArrayList<ArrayList<String>> meanings;

    private Dictionary(InputStream dictionaryStream) {
        parseDictionaryFile(dictionaryStream);
    }

    public static Dictionary getDictionary(InputStream dictionaryStream) {
        if (singleton == null) {
            singleton = new Dictionary(dictionaryStream);
        }

        return singleton;
    }

    private void parseDictionaryFile(InputStream dictionaryStream) {
        // index 0: hanzi
        // index 1: pinyin
        // index 2: meaning

        BufferedReader reader = new BufferedReader(new InputStreamReader(dictionaryStream));

        try {
//            dictionary = new Hashtable<>();
            String line;
            String[] delimited = new String[2];

            while (reader.ready()) {
                line = reader.readLine();

                if (!line.startsWith("#")) {
                    delimited = line.split(" ", 2);
                    simplified.add(delimited[0]);

                    delimited = delimited[1].split(" ", 2);
                    traditional.add(delimited[0]);

                    delimited = delimited[1].split("] /", 2);
                    pinyin.add(delimited[0]);

                    meaningsUnsplit.add(delimited[1]);

                    delimited = delimited[1].split("/", 2);
                    meanings.add(new ArrayList<String>(Arrays.asList(delimited)));
                }
            }

            reader.close();
            Log.d("custom", "Length simplified: " + simplified.size());
            Log.d("custom", "Length traditional: " + traditional.size());
            Log.d("custom", "Length pinyin: " + pinyin.size());
            Log.d("custom", "Length meaningsUnsplit: " + meaningsUnsplit.size());
            Log.d("custom", "Length meanings: " + meanings.size());
            Log.d("custom", "Finished parsing dictionary resource.");
        } catch (IOException e) {
            // TODO: catch or throw exception
            Log.d("custom", "Couldn't parse dictionary resource.");
        }
    }

    // TODO: implement
    // TODO: multiple results
    public String[] read(String search) {
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
}
