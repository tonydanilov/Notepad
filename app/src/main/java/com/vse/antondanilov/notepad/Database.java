package com.vse.antondanilov.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Database extends SQLiteOpenHelper {

    private static List<Note> notes;

    private static final int    DATABASE_VERSION    = 1;
    private static final String DATABASE_NAME       = "notes_DB";
    private static final String TABLE_NOTES         = "notes";
    private static final String TABLE_HASHTAGS      = "hashtags";
    private static final String TABLE_NOTES_HASHTAGS= "notes_hashtags";
    private static final String COLUMN_ID           = "id";
    private static final String COLUMN_NOTES_ID     = "notes_id";
    private static final String COLUMN_HASHTAGS_ID  = "hashtags_id";
    private static final String COLUMN_TITLE        = "note_title";
    private static final String COLUMN_TEXT         = "note_text";
    private static final String COLUMN_COLOR        = "note_color";
    private static final String COLUMN_CHECKBOXES   = "note_checkboxes";
    private static final String COLUMN_NAME         = "hashtag_name";

    //TODO všechny sql dotazy do proměnných

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        generateRandomValues();
        addDefaultHashtags();
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

    private void addDefaultHashtags() {
        if(getHashtags().size()>=3) return;
        Hashtag hashtag1 = new Hashtag(-1, "WORK");
        insertNewHashtag(hashtag1);

        Hashtag hashtag2 = new Hashtag(-1, "SCHOOL");
        insertNewHashtag(hashtag2);

        Hashtag hashtag3 = new Hashtag(-1, "INSPIRATION");
        insertNewHashtag(hashtag3);

    }

    List<Note> getNotes(int... hashtagId) {
        if(hashtagId.length == 0 ) {
            return selectAllNotes();
        }
        return selectNotesForHashtag(hashtagId[0]);
    }


    List<Hashtag> getHashtags() {
        return selectAllHashtags();
    }

    List<Hashtag> getHashtagsForNote(int noteId) {
        return selectHashtagsForNote(noteId);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT," +
                COLUMN_TEXT+ " TEXT," + COLUMN_COLOR + " INTEGER," + COLUMN_CHECKBOXES + " BOOLEAN" + ")";

        String CREATE_HASHTAGS_TABLE = "CREATE TABLE " + TABLE_HASHTAGS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT)";

        String CREATE_NOTES_HASHTAGS_TABLE = "CREATE TABLE " + TABLE_NOTES_HASHTAGS + "(" +
                COLUMN_NOTES_ID + " INTEGER," + COLUMN_HASHTAGS_ID + " INTEGER, FOREIGN KEY (" + COLUMN_NOTES_ID + ") REFERENCES " +
                TABLE_NOTES + "(" + COLUMN_ID + "), FOREIGN KEY (" + COLUMN_HASHTAGS_ID + ") REFERENCES " + TABLE_HASHTAGS +
                "(" + COLUMN_ID + "))";

        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_HASHTAGS_TABLE);
        db.execSQL(CREATE_NOTES_HASHTAGS_TABLE);
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

    private List<Hashtag> selectAllHashtags() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Hashtag> returnHashtags = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_HASHTAGS, null);
        if(cur.moveToFirst()) {
            do{
                int id = Integer.parseInt(cur.getString(0));
                String name = cur.getString(1);
                returnHashtags.add(new Hashtag(id,name));
            } while (cur.moveToNext());
        }
        cur.close();
        return returnHashtags;
    }

    private List<Hashtag> selectHashtagsForNote(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Hashtag> returnHashtags = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_NAME + " FROM " + TABLE_HASHTAGS + " JOIN " + TABLE_NOTES_HASHTAGS + " ON " +
                TABLE_NOTES_HASHTAGS + "." + COLUMN_HASHTAGS_ID + " = " + TABLE_HASHTAGS + "." + COLUMN_ID + " WHERE " +
                TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = ?", new String[] {String.valueOf(noteId)});
        if(cur.moveToFirst()) {
            do{
                int id = Integer.parseInt(cur.getString(0));
                String name = cur.getString(1);
                returnHashtags.add(new Hashtag(id,name));
            } while (cur.moveToNext());
        }
        cur.close();
        return returnHashtags;
    }

    private List<Note> selectNotesForHashtag(int hashtagId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> returnNotes = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " JOIN " + TABLE_NOTES_HASHTAGS + " ON " +
                TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = " + TABLE_NOTES + "." + COLUMN_ID + " WHERE " +
                TABLE_NOTES_HASHTAGS + "." + COLUMN_HASHTAGS_ID + " = ?", new String[] {String.valueOf(hashtagId)});

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



    int insertNewNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_TEXT, note.getText());
        values.put(COLUMN_COLOR, note.getColor().getId());
        values.put(COLUMN_CHECKBOXES, note.isCheckbox());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTES, null, values);

        return selectAllNotes().size(); //==id of last insert
    }

    void insertNewHashtag(Hashtag hashtag) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, hashtag.getName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HASHTAGS, null, values);
    }

    void addHashtagToNote(int noteId, int hashtagId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTES_ID, noteId);
        values.put(COLUMN_HASHTAGS_ID, hashtagId);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTES_HASHTAGS, null, values);
    }

    void removeHashtagFromNote(int noteId, int hashtagId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES_HASHTAGS, COLUMN_NOTES_ID + " = ? AND " + COLUMN_HASHTAGS_ID + " = ?",
                new String[] {String.valueOf(noteId), String.valueOf(hashtagId)});
    }

    void deleteHashtag(int hashtagId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES_HASHTAGS, COLUMN_HASHTAGS_ID + " = ?", new String[] {String.valueOf(hashtagId)});
        db.delete(TABLE_HASHTAGS, COLUMN_ID + " = ?", new String[] {String.valueOf(hashtagId)});
    }

    void deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES_HASHTAGS, COLUMN_NOTES_ID + " = ?", new String[] {String.valueOf(noteId)});
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[] {String.valueOf(noteId)});
    }

    void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_TEXT, note.getText());
        values.put(COLUMN_COLOR, note.getColor().getId());
        values.put(COLUMN_CHECKBOXES, note.isCheckbox());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(note.getId())});
    }
}