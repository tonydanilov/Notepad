package com.vse.antondanilov.notepad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static Database DB;
    public static String    NOTE_ID = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_hashtags);
        setSupportActionBar(toolbar);

        DB = new Database(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();
                System.out.println("== NOTE creating new note");
                showNote(-1);
            }
        });

     generateTableItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update
        generateTableItems();
    }

    private void generateTableItems() {
        TableLayout table = (TableLayout) this.findViewById(R.id.main_menu_table);
        table.removeAllViews();

        final List<Note> notes = DB.getNotes();
        for(final Note note : notes) {
            LinearLayout tableRow = (LinearLayout) View.inflate(this, R.layout.main_menu_item, null);

            Random rand = new Random();
            int k = rand.nextInt(9);

            tableRow.setBackgroundColor(Color.parseColor(note.getHexColor()));
            TextView title = tableRow.findViewById(R.id.note_title);
            TextView noteText = tableRow.findViewById(R.id.note_text);

            title.setText(note.getTitle());
            noteText.setPadding(noteText.getPaddingLeft(), 0,0, k * 100);
            noteText.setText(note.getText());

            table.addView(tableRow);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNote(notes.indexOf(note));
                }
            });
        }
    }

    private void showNote(int noteId) {
        Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
        intent.putExtra(NOTE_ID, noteId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Database getDB() {
        return DB;
    }
}
