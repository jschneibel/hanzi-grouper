package com.example.hanzi_grouper;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dictionary {
    private static Dictionary singleton;

    // Data of each column gets loaded into its own ArrayList.
    private ArrayList<String> simplified = new ArrayList<>();
//    private ArrayList<String> traditional = new ArrayList<>();
    private ArrayList<String> pinyin = new ArrayList<>();
    private ArrayList<String> meaningsUnsplit = new ArrayList<>();

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
            String line;
            String[] delimited;

            while (reader.ready()) {
                line = reader.readLine();

                // Discard comments.
                if (!line.startsWith("#")) {
                    delimited = line.split(" ", 2);

                    // Only include single characters.
                    if (delimited[0].length() == 1) {
//                        traditional.add(delimited[0]);

                        delimited = delimited[1].split(" \\[", 2);
                        simplified.add(delimited[0]);

                        delimited = delimited[1].split("\\] \\/", 2);
                        pinyin.add(delimited[0].replace("u:", "ü"));

                        // Remove trailing forward slash.
                        delimited[1] = delimited[1].substring(0, delimited[1].length() - 1);
                        meaningsUnsplit.add(delimited[1]);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            Log.d("custom", "Couldn't parse dictionary resource.");
        }
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
}
