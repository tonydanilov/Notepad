package com.vse.antondanilov.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static Database  DB;
    public static String    NOTE_ID = "note_id";
    private static int      NEW_NOTE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_acitivty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DB = new Database(this);

        createNewNoteButton();
        loadNotes();
        createDrawerMenu(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createDrawerMenuItems();
        loadNotes();
    }

    private void createNewNoteButton() {
        FloatingActionButton newNoteFloatingButton = (FloatingActionButton) findViewById(R.id.new_note_button);
        newNoteFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNote(NEW_NOTE);
            }
        });
    }

    private void createDrawerMenu(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle); //TODO check this
        toggle.syncState();
        createDrawerMenuItems();
    }

    private void createDrawerMenuItems() {
        List<String> list = new ArrayList<>(); //TODO better

        list.add(getString(R.string.drawer_menu_select_all));
        for (Hashtag hashtag : DB.getHashtags()) {
            list.add(hashtag.getName());
        }

        ListView lv = (ListView) findViewById(R.id.navigation_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_drawer, list);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadNotes(position);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void loadNotes(int... hashtagId) {
        TableLayout table = (TableLayout) this.findViewById(R.id.main_menu_table);
        table.removeAllViews();

        final List<Note> notes;
        if(hashtagId.length == 0) {
            notes = MainActivity.getDB().getNotes();
        } else {
            notes = MainActivity.getDB().getNotes(hashtagId[0]);
        }


        for(final Note note : notes) {
            LinearLayout tableRow = (LinearLayout) View.inflate(this, R.layout.item_main_menu, null);

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

            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createDeleteDialog(note);
                    return false;
                }
            });
        }
    }

    private void createDeleteDialog(final Note note) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.dialog_delete_question) + " \"" +note.getTitle() + "\"?");
        builder.setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(note.getId());
                loadNotes(0);
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteNote(int noteId) {
        MainActivity.getDB().deleteNote(noteId);
    }

    private void showNote(int noteId) {
        Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
        intent.putExtra(NOTE_ID, noteId);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static Database getDB() {
        return DB;
    }
}
