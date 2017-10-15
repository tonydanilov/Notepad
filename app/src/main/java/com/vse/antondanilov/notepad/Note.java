package com.vse.antondanilov.notepad;

import java.util.Random;

/**
 * Created by tonyd on 15.10.2017.
 */

public class Note {
    private String title;
    private String text;
    private boolean checkboxes;
    private int color;

    public Note(String title, String text, boolean checkboxes) {
        this.title = title;
        this.text = text;
        this.checkboxes = checkboxes;
        this.color = randomizeColor();
    }

    public Note(String text, boolean checkboxes) {
        new Note("", text, checkboxes);
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

    public boolean isCheckboxes() {
        return checkboxes;
    }

    public int getColor() {
        return color;
    }

    public String getHexColor() {
        return "#" + color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
