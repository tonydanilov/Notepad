package com.vse.antondanilov.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tonyd on 15.10.2017.
 */

public class Database extends SQLiteOpenHelper {

    private static List<Note> notes;

    private static final int    DATABASE_VERSION    = 1;
    private static final String DATABASE_NAME       = "notes_DB";
    private static final String TABLE_NOTES         = "notes";
    private static final String COLUMN_ID           = "id";
    private static final String COLUMN_TITLE        = "note_title";
    private static final String COLUMN_TEXT         = "note_text";
    private static final String COLUMN_COLOR        = "note_color";
    private static final String COLUMN_CHECKBOXES   = "note_checkboxes";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        generateRandomValues();
    }

    private void generateRandomValues() {
        int size = selectAllNotes().size();
        Random rand = new Random();
        while (size < 20) {
            String tt = String.valueOf(rand.nextFloat());
            int c = rand.nextInt(6);
            String t = (size++ % 2 == 0 ? "Bureš" : "Eman");
            boolean b = rand.nextBoolean();
            Note note = new Note(size - 1, t, tt, NoteColor.getNoteColorForId(c), b);
            insertNewNote(note);
        }
    }

    public List<Note> getNotes() {
        return selectAllNotes();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT," +
                COLUMN_TEXT+ " TEXT," + COLUMN_COLOR + " INTEGER," + COLUMN_CHECKBOXES + " BOOLEAN" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    //TODO do I need it?
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    private List<Note> selectAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> returnNotes = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        if(cur.moveToFirst()) {
            do{
                int id = Integer.parseInt(cur.getString(0));
                String title = cur.getString(1);
                String text = cur.getString(2);
                int color = Integer.parseInt(cur.getString(3));
                boolean checkboxes = Boolean.parseBoolean(cur.getString(4));
                returnNotes.add(new Note(id,title,text,NoteColor.getNoteColorForId(color),checkboxes));
            } while (cur.moveToNext());
        }
        cur.close();
        return returnNotes;
    }

    private void insertNewNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_TEXT, note.getText());
        values.put(COLUMN_COLOR, note.getColor().getId());
        values.put(COLUMN_CHECKBOXES, note.isCheckbox());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTES, null, values);
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_TEXT, note.getText());
        values.put(COLUMN_COLOR, note.getColor().getId());
        values.put(COLUMN_CHECKBOXES, note.isCheckbox());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(note.getId())});
    }
}