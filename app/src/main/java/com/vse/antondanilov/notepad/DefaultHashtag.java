package com.vse.antondanilov.notepad;

enum DefaultHashtag {
    SCHOOL("School"),
    WORK("Work"),
    INSPIRATION("Inspiration");

    private String name;

    DefaultHashtag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
