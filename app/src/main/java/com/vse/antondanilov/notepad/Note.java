package com.vse.antondanilov.notepad;

import java.util.Random;

/**
 * Created by tonyd on 15.10.2017.
 */

public class Note {
    private int id;
    private String title;
    private String text;
    private boolean checkbox;
    private NoteColor color;

    public Note(int id, String title, String text, NoteColor color, boolean checkbox) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.checkbox = checkbox;
        this.color = color;
    }

    public Note(int id, String text, NoteColor color, boolean checkbox) {
        new Note(id, "", text, color, checkbox);
    }

    public Note(int id, String text, boolean checkbox) {
        new Note(id, "", text, NoteColor.WHITE, checkbox);
    }

    private int randomizeColor() {
        Random rand = new Random();
        int returnColor = 0;
        for(int i = 0; i<6; i++) {
            returnColor += (rand.nextInt(9) * Math.pow(10, i));
        }
        while(returnColor < 10000000) returnColor *= 10;
        return returnColor;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public NoteColor getColor() {
        return color;
    }

    public String getHexColor() {
        return color.getHexColor();
    }

    public void setColor(NoteColor color) {
        this.color = color;
    }


}
