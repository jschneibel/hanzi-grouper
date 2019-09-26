package com.example.hanzi_grouper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private ArrayList<String> characters = new ArrayList<>();
    private ArrayList<String> pinyin = new ArrayList<>();
    private ArrayList<String> meanings = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, List<String> characters, List<String> pinyin, List<String> meanings) {
        this(name);
        setEntries(characters, pinyin, meanings);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getCharacters() {
        return new ArrayList<String>(characters);
    }

    public ArrayList<String> getPinyin() {
        return new ArrayList<String>(pinyin);
    }

    public ArrayList<String> getMeanings() {
        return new ArrayList<String>(meanings);
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        else {
            this.name = name;
        }
    }

    public void setEntries(List<String> characters,
                           List<String> pinyin,
                           List<String> meanings) {

        // commented because setEntries should throw exception when passed null
//        characters = (characters == null) ? new ArrayList<String>() : characters;
//        pinyin = (pinyin == null) ? new ArrayList<String>() : pinyin;
//        meanings = (meanings == null) ? new ArrayList<String>() : meanings;

        if ((characters.size() == pinyin.size()) && (pinyin.size() == meanings.size())) {
            this.characters = new ArrayList<>(characters);
            this.pinyin = new ArrayList<>(pinyin);
            this.meanings = new ArrayList<>(meanings);
        }
        else {
            throw new IllegalArgumentException(
                    "ArrayLists characters, pinyin and meanings have to be of the same size.");
        }
    }

    public void addEntry(String character, String pinyin, String meaning) {
        if ((character != null) && (pinyin != null) && (meaning != null)) {
            this.characters.add(character);
            this.pinyin.add(pinyin);
            this.meanings.add(meaning);
        } else {
            throw new IllegalArgumentException("Null input in addEntry() not allowed.");
        }
    }

    public void removeEntry(String character) {
        int characterIndex = this.characters.indexOf(character);

        if (characterIndex == -1) {
            throw new IllegalArgumentException(
                    "Group does not contain character to be removed.");
        } else {
            this.characters.remove(characterIndex);
            this.pinyin.remove(characterIndex);
            this.meanings.remove(characterIndex);
        }
    }

    public int size() {
        if (characters == null) {
            return 0;
        }
        else {
            return characters.size();
        }
    }
}
