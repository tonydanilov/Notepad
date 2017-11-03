package com.vse.antondanilov.notepad;

class Note {
    private int id;
    private String title;
    private String text;
    private NoteColor color;

    Note(int id, String title, String text, NoteColor color) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setText(String text) {
        this.text = text;
    }

    public NoteColor getColor() {
        return color;
    }

    String getHexColor() {
        return color.getHexColor();
    }

    public void setColor(NoteColor color) {
        this.color = color;
    }
}
