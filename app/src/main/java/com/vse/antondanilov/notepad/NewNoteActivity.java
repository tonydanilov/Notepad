package com.vse.antondanilov.notepad;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import static com.vse.antondanilov.notepad.MainActivity.NOTE_ID;

public class NewNoteActivity extends AppCompatActivity {

    private Note note;
    private EditText title;
    private EditText text;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        if (getIntent().getExtras().getInt(NOTE_ID) == -1) {
            note = new Note(-1, "", "", NoteColor.WHITE, false);
        } else {
            note = MainActivity.getDB().getNotes().get(getIntent().getExtras().getInt(NOTE_ID));
        }

        initUI();
        loadNote();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
                finish();
            }
        });
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (EditText) findViewById(R.id.open_note_title);
        text = (EditText) findViewById(R.id.open_note_text);
    }

    private void loadNote() {
        title.setText(note.getTitle());
        text.setText(note.getText());
        toolbar.setBackgroundColor(Color.parseColor(note.getHexColor()));
    }

    private void saveNote() {
        //toolbar.setColor ??
        note.setTitle(title.getText().toString());
        note.setText(text.getText().toString());

        if(note.getId() == -1) {
            note.setColor(NoteColor.RED);
            MainActivity.getDB().insertNewNote(note);
        } else {
            MainActivity.getDB().updateNote(note);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNote();
    }
}
