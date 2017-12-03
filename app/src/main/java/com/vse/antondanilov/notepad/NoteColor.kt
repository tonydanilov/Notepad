package com.vse.antondanilov.notepad

internal enum class NoteColor constructor(val id: Int, val hexColor: String) {
    WHITE(0, "#cccccc"),
    BLACK(1, "#000000"),
    BLUE(2, "#0000cc"),
    RED(3, "#550000"),
    GREEN(4, "#005500"),
    YELLOW(5, "#bbbb00");


    companion object {

        fun getNoteColorForId(id: Int): NoteColor? {
            return values().first { it.id == id }
        }
    }
}
