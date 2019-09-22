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
    private ArrayList<String> characters = new ArrayList<>();
    private ArrayList<String> leftComponents = new ArrayList<>();
    private ArrayList<String> rightComponents = new ArrayList<>();

    private Decompositions(InputStream decompositionsStream) {
        parseDecompositionsFile(decompositionsStream);
    }

    public static Decompositions getDecompositions(InputStream decompositionsStream) {
        if (singleton == null) {
            Log.d("custom", "Parsing decompositions file.");
            singleton = new Decompositions(decompositionsStream);
            Log.d("custom", "Finished parsing decompositions file.");
        } else {
            Log.d("custom", "Returning previously loaded decompositions.");
        }

        return singleton;
    }

    private void parseDecompositionsFile(InputStream decompositionsStream) {

//      Columns in file:
//      0 Character
//      1 Strokes
//      2 CompositionType
//      3 LeftComponent (can be multiple characters)
//      4 LeftStrokes
//      5 RightComponent (can be multiple characters)
//      6 RightStrokes
//      7 Signature
//      8 Notes
//      9 Section

        BufferedReader reader = new BufferedReader(new InputStreamReader(decompositionsStream));

        try {
            String line;
            String[] delimited;

            while (reader.ready()) {
                line = reader.readLine();

                delimited = line.split("\\t", 10);

                characters.add(delimited[0]);
                leftComponents.add(delimited[3]);
                rightComponents.add(delimited[5]);
            }

            reader.close();
        } catch (IOException e) {
            Log.d("custom", "Couldn't parse decompositions resource.");
        }
    }

    public ArrayList<String> findSimilarCharacters(String search) {
        ArrayList<String> result = new ArrayList<>();

        if (singleton == null) {
            return null;    // return null if dictionary is not loaded yet
        }

//        int index = simplified.indexOf(search);
//
//        if (index != -1) {
//            result.add(simplified.get(index));
//            result.add(pinyin.get(index));
//            result.add(meaningsUnsplit.get(index));
//
//            return result;
//        }

        return null;    // return null if character is not found
    }

}
