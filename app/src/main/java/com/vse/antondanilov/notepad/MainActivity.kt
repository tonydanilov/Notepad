package com.vse.antondanilov.notepad

import android.content.Intent
import android.graphics.Color
import android.os.Bundle

import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TextView

import java.util.ArrayList

import com.vse.antondanilov.notepad.Constants.ALL_NOTES
import com.vse.antondanilov.notepad.Constants.NEW_NOTE_DEFAULT_VALUE
import com.vse.antondanilov.notepad.Constants.NOTE_ID

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_acitivty)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        Database.init(this)

        createNewNoteButton()
        loadNotes(ALL_NOTES)
        createDrawerMenu(toolbar)
    }

    override fun onResume() {
        super.onResume()
        createDrawerMenuItems()
        loadNotes(ALL_NOTES)
    }

    private fun createNewNoteButton() {
        val newNoteFloatingButton = findViewById(R.id.new_note_button) as FloatingActionButton
        newNoteFloatingButton.setOnClickListener { showNote(NEW_NOTE_DEFAULT_VALUE) }
    }

    private fun createDrawerMenu(toolbar: Toolbar) {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        createDrawerMenuItems()
    }

    private fun createDrawerMenuItems() {
        val list = ArrayList<String>()
        list.add(getString(R.string.drawer_menu_select_all))
        for (hashtag in Database.getInstance(this)!!.hashtags) {
            list.add(hashtag.hashtagName)
        }

        val lv = findViewById(R.id.navigation_list_view) as ListView
        val arrayAdapter = ArrayAdapter(this, R.layout.item_drawer, list)
        lv.adapter = arrayAdapter

        lv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            loadNotes(position)
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    private fun loadNotes(hashtagId: Int) {
        val table = this.findViewById(R.id.main_menu_table) as TableLayout
        table.removeAllViews()

        val notes = Database.getInstance(this)!!.getNotes(hashtagId)
        for (note in notes) {
            val tableRow = View.inflate(this, R.layout.item_main_menu, null) as LinearLayout

            tableRow.setBackgroundColor(Color.parseColor(note.hexColor))
            val title = tableRow.findViewById<TextView>(R.id.note_title)
            val noteText = tableRow.findViewById<TextView>(R.id.note_text)
            title.text = note.title
            noteText.text = note.text

            table.addView(tableRow)

            tableRow.setOnClickListener { showNote(note.id) }

            tableRow.setOnLongClickListener {
                createDeleteDialog(note)
                false
            }
        }
    }

    private fun createDeleteDialog(note: Note) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.dialog_delete_question) + " \"" + note.title + "\"?")
        builder.setPositiveButton(R.string.dialog_delete_button) { _, _ ->
            deleteNote(note.id)
            loadNotes(ALL_NOTES)
        }

        builder.setNegativeButton(R.string.dialog_cancel_button) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun deleteNote(noteId: Int) {
        Database.getInstance(this)!!.deleteNote(noteId)
    }

    private fun showNote(noteId: Int) {
        val intent = Intent(this@MainActivity, NewNoteActivity::class.java)
        intent.putExtra(NOTE_ID, noteId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
