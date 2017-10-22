package com.vse.antondanilov.notepad;

/**
 * Created by tonyd on 17.10.2017.
 */

public enum NoteColor {
    WHITE   (0,"#ffffff"),
    BLACK   (1,"#000000"),
    BLUE    (2,"#0000ff"),
    RED     (3,"#ff0000"),
    GREEN   (4,"#00ff00"),
    YELLOW  (5,"#aaaa00"),
    GREY    (6,"#222222");

    private int id;
    private String hexColor;

    NoteColor(int id, String hexColor) {
        this.id = id;
        this.hexColor = hexColor;
    }

    public int getId() {
        return id;
    }

    public String getHexColor() {
        return hexColor;
    }

    public static NoteColor getNoteColorForId(int id) {
        for(NoteColor noteColor : values()) {
            if(noteColor.getId() == id) return noteColor;
        }
        return null;
    }
}
