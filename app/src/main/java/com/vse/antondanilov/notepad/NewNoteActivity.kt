package com.vse.antondanilov.notepad

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout

import com.vse.antondanilov.notepad.Constants.ALL_NOTES
import com.vse.antondanilov.notepad.Constants.NEW_NOTE_DEFAULT_VALUE
import com.vse.antondanilov.notepad.Constants.NOTE_ID

class NewNoteActivity : AppCompatActivity() {
    
    private lateinit var title: EditText
    private lateinit var text: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var note: Note
    private lateinit var colorDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        note = if (intent.extras.getInt(NOTE_ID) == NEW_NOTE_DEFAULT_VALUE) {
            Note(NEW_NOTE_DEFAULT_VALUE, "", "", NoteColor.WHITE)
        } else {
            Database.getInstance(this).getNotes(ALL_NOTES)[intent.extras.getInt(NOTE_ID) - 1]
        }

        initUI()
        loadNote()

        val fabHashtag = findViewById(R.id.fab_hashtag) as FloatingActionButton
        fabHashtag.setOnClickListener {
            saveNote()
            val intent = Intent(this@NewNoteActivity, HashtagsActivity::class.java)
            intent.putExtra(NOTE_ID, note.id)
            startActivity(intent)
        }

        val fabColor = findViewById(R.id.fab_color) as FloatingActionButton
        fabColor.setOnClickListener {
            val builder = AlertDialog.Builder(this@NewNoteActivity)
            builder.setTitle(R.string.pick_color_title)

            val colorPickerLayout = View.inflate(this@NewNoteActivity, R.layout.layout_color_picker, null) as LinearLayout
            for (noteColor in NoteColor.values()) {
                val button = Button(this@NewNoteActivity)
                button.setBackgroundColor(Color.parseColor(noteColor.hexColor))
                button.setOnClickListener {
                    note.color = noteColor
                    saveNote()
                    colorDialog.cancel()
                }
                colorPickerLayout.addView(button)
            }

            builder.setView(colorPickerLayout)
            colorDialog = builder.create()
            colorDialog.show()
        }
        saveNote()
    }

    private fun initUI() {
        toolbar = findViewById(R.id.toolbar_hashtags) as Toolbar
        setSupportActionBar(toolbar)
        title = findViewById(R.id.open_note_title) as EditText
        text = findViewById(R.id.open_note_text) as EditText
    }

    private fun loadNote() {
        title.setText(note.title)
        text.setText(note.text)
        toolbar.setBackgroundColor(Color.parseColor(note.hexColor))
    }

    private fun saveNote() {
        note.title = title.text.toString()
        note.text = text.text.toString()

        if (note.id == NEW_NOTE_DEFAULT_VALUE) {
            note.id = Database.getInstance(this).insertNewNote(note)
        } else {
            Database.getInstance(this).updateNote(note)
        }
        refreshNote()
    }

    private fun refreshNote() {
        toolbar.setBackgroundColor(Color.parseColor(note.hexColor))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveNote()
    }
}
