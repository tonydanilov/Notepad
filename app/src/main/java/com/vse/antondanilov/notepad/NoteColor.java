package com.vse.antondanilov.notepad;

enum NoteColor {
    GREY    (0,"#cccccc"),
    BLACK   (1,"#000000"),
    BLUE    (2,"#0000cc"),
    RED     (3,"#881111"),
    GREEN   (4,"#005500"),
    YELLOW  (5,"#bbbb00");

    private final int id;
    private final String hexColor;

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

    public static NoteColor getDefaultColor() {
        return GREY;
    }
}
