package com.vse.antondanilov.notepad;

/**
 * Created by tonyd on 17.10.2017.
 */

public enum NoteColor {
    GREY    (0,"#cccccc"),
    BLACK   (1,"#000000"),//
    BLUE    (2,"#0000cc"),
    RED     (3,"#550000"),//
    GREEN   (4,"#005500"),//
    YELLOW  (5,"#bbbb00");

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
