package com.example.hanzi_grouper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

//      Note: The characters are not always divided into left and right, they can also be divided
//      into top and bottom. However, the labels used are always LeftComponent and RightComponent.

//      Note: Contains simplified and traditional characters.

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
        ArrayList<ArrayList<String>> results = new ArrayList<>();

        // one result stores (0) character, (1) left component and (2) right component
        ArrayList<String> result;

//        results.add(new ArrayList<String>());    // result characters
//        results.add(new ArrayList<String>());    // result left components
//        results.add(new ArrayList<String>());    // result left strokes
//        results.add(new ArrayList<String>());    // result right components
//        results.add(new ArrayList<String>());    // result right strokes

        if (singleton == null) {
            return null;    // return null if dictionary is not loaded yet
        }

        int searchIndex = characters.indexOf(search);
        String searchLeftComponent = leftComponents.get(searchIndex);
        String searchRightComponent = rightComponents.get(searchIndex);

        for (int i = 0; i < characters.size(); i++) {
            if (leftComponents.get(i).equals(search)
                    || leftComponents.get(i).equals(searchLeftComponent)
                    || leftComponents.get(i).equals(searchRightComponent)
                    || rightComponents.get(i).equals(search)
                    || rightComponents.get(i).equals(searchLeftComponent)
                    || rightComponents.get(i).equals(searchRightComponent)
                    || characters.get(i).equals(searchLeftComponent)
                    || characters.get(i).equals(searchRightComponent)) {
                if (!characters.get(i).equals(search)) {
                    result = new ArrayList<>();
                    result.add(characters.get(i));
                    result.add(leftComponents.get(i));
                    result.add(rightComponents.get(i));

                    results.add(result);
                }
            }
        }

        if (results.isEmpty()) {
            return null;    // return null if character is not found
        } else {
            ArrayList<String> comparisonSearch = new ArrayList<>();
            comparisonSearch.add(search);
            comparisonSearch.add(searchLeftComponent);
            comparisonSearch.add(searchRightComponent);
            results.sort(new CharacterComparator(comparisonSearch)); // sort from low to high similarity
            Collections.reverse(results);   // sort from high to low similarity

            // only return characters (without component info)
            ArrayList<String> resultCharacters = new ArrayList<>();
            for (ArrayList<String> r : results) {
                resultCharacters.add(r.get(0));
            }

            return resultCharacters;
        }
    }

    public class CharacterComparator implements Comparator<ArrayList<String>> {
        // sorts from low similarity to high similarity to search character

        // one ArrayList stores (0) character, (1) left component and (2) right component
        private ArrayList<String> search;

        public CharacterComparator(ArrayList<String> search) {
            this.search = search;
        }

        @Override
        public int compare(ArrayList<String> characterA, ArrayList<String> characterB) {
            // higher score means higher similarity
            int scoreA = 0;
            int scoreB = 0;

            if (search.get(2).equals(characterA.get(2))) scoreA += 4;
            if (search.get(0).equals(characterA.get(2))) scoreA += 3;
            if (search.get(2).equals(characterA.get(0))) scoreA += 3;
            if (search.get(1).equals(characterA.get(1))) scoreA += 2;
            if (search.get(0).equals(characterA.get(1))) scoreA += 1;
            if (search.get(1).equals(characterA.get(0))) scoreA += 1;

            if (search.get(2).equals(characterB.get(2))) scoreA += 4;
            if (search.get(0).equals(characterB.get(2))) scoreA += 3;
            if (search.get(2).equals(characterB.get(0))) scoreA += 3;
            if (search.get(1).equals(characterB.get(1))) scoreA += 2;
            if (search.get(0).equals(characterB.get(1))) scoreA += 1;
            if (search.get(1).equals(characterB.get(0))) scoreA += 1;

            return scoreA - scoreB;
        }
    }
}
