package com.vse.antondanilov.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Database DB;
    public static String    NOTE_ID = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_acitivty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DB = new Database(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNote(-1);
            }
        });

        generateTableItems(0);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle); //TODO check this
        toggle.syncState();

        createDrawerMenuItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update

        createDrawerMenuItems();
        generateTableItems(0);
    }

    private void createDrawerMenuItems() {
        List<String> list = new ArrayList<>(); //TODO better

        list.add("ALL");
        for (Hashtag hashtag : DB.getHashtags()) {
            list.add(hashtag.getName());
        }

        ListView lv = (ListView) findViewById(R.id.navigation_list_view);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_drawer, list);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                generateTableItems((position));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void generateTableItems(int hashtagId) {
        TableLayout table = (TableLayout) this.findViewById(R.id.main_menu_table);
        table.removeAllViews();

        final List<Note> notes;
        if(hashtagId == 0 ) {
            notes = MainActivity.getDB().getNotes();
        } else {
            notes = MainActivity.getDB().getNotes(hashtagId);
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
                    creatDeleteDialog(note);
                    return false;
                }
            });
        }
    }

    private void creatDeleteDialog(final Note note) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete note \"" + note.getTitle() + "\"?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(note.getId());
                generateTableItems(0);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_acitivty, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Database getDB() {
        return DB;
    }
}
