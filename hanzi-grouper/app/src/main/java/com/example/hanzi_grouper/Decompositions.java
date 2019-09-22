package com.example.hanzi_grouper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Decompositions {
    private static Decompositions singleton;

    // Data of each column gets loaded into its own ArrayList.
    private ArrayList<String> simplified = new ArrayList<>();

    private Decompositions(InputStream decompositionsStream) {
        parseDecompositionsFile(decompositionsStream);
    }

    public static Decompositions getDecompositions(InputStream decompositionsStream) {
        if (singleton == null) {
            Log.d("custom", "Parsing decompositions file.");
            singleton = new Decompositions(decompositionsStream);
            Log.d("custom", "Finished parsing decompositions file.");
        }
        else {
            Log.d("custom", "Returning previously loaded decompositions.");
        }

        return singleton;
    }

    private void parseDecompositionsFile(InputStream decompositionsStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(decompositionsStream));

//      Columns in file:
//      Character
//      Strokes
//      CompositionType
//      LeftComponent
//      LeftStrokes
//      RightComponent
//      RightStrokes
//      Signature
//      Notes
//      Section

        try {
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
