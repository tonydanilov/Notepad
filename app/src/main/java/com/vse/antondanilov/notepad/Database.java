package com.vse.antondanilov.notepad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyd on 15.10.2017.
 */

public class Database {

    private static List<Note> notes;

    public Database() {
        notes = new ArrayList<>();
        for(int i = 0; i < 25; i++) {
            String t = i % 2 == 0 ? "Bureš" : "Eman";
            Note note = new Note("ID " + i, t, false);
            notes.add(note);
        }
    }

    public List<Note> getNotes() {
        return notes;
    }
}
