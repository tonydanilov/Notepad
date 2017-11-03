package com.vse.antondanilov.notepad

internal class Note(var id: Int, var title: String?, var text: String?, var color: NoteColor?) {

    val hexColor: String
        get() = color!!.hexColor
}
