package com.vse.antondanilov.notepad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

        FloatingActionButton fabHashtag = (FloatingActionButton) findViewById(R.id.fab_hashtag);
        fabHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  saveNote();
                //   finish();
                Intent intent = new Intent(NewNoteActivity.this, HashtagsActivity.class);
                intent.putExtra(NOTE_ID, note.getId());
                startActivity(intent);
            }
        });

        FloatingActionButton fabColor = (FloatingActionButton) findViewById(R.id.fab_color);
        fabColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewNoteActivity.this);
                builder.setTitle("Pick color"); //TODO

                LinearLayout colorPickerLayout = (LinearLayout) View.inflate(NewNoteActivity.this, R.layout.layout_color_picker, null);
                for (final NoteColor noteColor : NoteColor.values()) {
                    Button button = new Button(NewNoteActivity.this);
                    button.setBackgroundColor(Color.parseColor(noteColor.getHexColor()));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            note.setColor(noteColor);
                            saveNote();
                            hideDialog();
                        }
                    });
                    colorPickerLayout.addView(button);
                }

                builder.setView(colorPickerLayout);
                colorDialog = builder.create();
                colorDialog.show();
            }
        });
    }

    AlertDialog colorDialog;

    private void hideDialog() {
        if(colorDialog != null) colorDialog.cancel();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_hashtags);
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

        if (note.getId() == -1) {
            note.setColor(NoteColor.RED);
            MainActivity.getDB().insertNewNote(note);
        } else {
            MainActivity.getDB().updateNote(note);
        }

        refreshNote();
    }

    private void refreshNote() {
        toolbar.setBackgroundColor(Color.parseColor(note.getHexColor()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNote();
    }
}
