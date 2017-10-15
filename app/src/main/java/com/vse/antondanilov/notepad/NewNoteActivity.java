package com.vse.antondanilov.notepad;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

public class NewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        final Note note = Database.getNotes().get(getIntent().getExtras().getInt("noteId"));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(note.getHexColor()));
        setSupportActionBar(toolbar);

        final EditText title = (EditText) findViewById(R.id.open_note_title);
        final EditText text = (EditText) findViewById(R.id.open_note_text);

        title.setText(note.getTitle());
        text.setText(note.getText());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     Snackbar.make(view, "Color changed", Snackbar.LENGTH_LONG)
            //            .setAction("Action", null).show();

                Random random = new Random();
                int k = random.nextInt(9);
                toolbar.setBackgroundColor(Color.parseColor("#"+k+k+k+k+k+k));
                note.setTitle(title.getText().toString());
                note.setText(text.getText().toString());
            }
        });
    }

}
