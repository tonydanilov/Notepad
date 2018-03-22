package com.vse.antondanilov.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.vse.antondanilov.notepad.Constants.ALL_NOTES;

class Database extends SQLiteOpenHelper {

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
    private static final String COLUMN_NAME         = "hashtag_name";

    private static final String ON_UPGRADE_DROP = "DROP TABLE IF EXISTS " + TABLE_NOTES;
    private static final String SELECT_ALL_NOTES = "SELECT * FROM " + TABLE_NOTES;
    private static final String SELECT_ALL_HASHTAGS = "SELECT * FROM " + TABLE_HASHTAGS;

    private static final String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT," +
            COLUMN_TEXT+ " TEXT," + COLUMN_COLOR + " INTEGER" + ")";

    private static final String CREATE_HASHTAGS_TABLE = "CREATE TABLE " + TABLE_HASHTAGS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT)";

    private static final String CREATE_NOTES_HASHTAGS_TABLE = "CREATE TABLE " + TABLE_NOTES_HASHTAGS
            + "(" + COLUMN_NOTES_ID + " INTEGER," + COLUMN_HASHTAGS_ID + " INTEGER, FOREIGN KEY (" +
            COLUMN_NOTES_ID + ") REFERENCES " + TABLE_NOTES + "(" + COLUMN_ID + "), FOREIGN KEY (" +
            COLUMN_HASHTAGS_ID + ") REFERENCES " + TABLE_HASHTAGS +
            "(" + COLUMN_ID + "))";

    private static final String SELECT_HASHTAGS_FOR_NOTE = "SELECT " + COLUMN_ID + ", " +
            COLUMN_NAME + " FROM " + TABLE_HASHTAGS + " JOIN " + TABLE_NOTES_HASHTAGS + " ON " +
            TABLE_NOTES_HASHTAGS + "." + COLUMN_HASHTAGS_ID + " = " + TABLE_HASHTAGS + "." + COLUMN_ID
            + " WHERE " + TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = ?";

    private static final String SELECT_NOTES_FOR_HASHTAG = "SELECT * FROM " + TABLE_NOTES + " JOIN "
            + TABLE_NOTES_HASHTAGS + " ON " + TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = " +
            TABLE_NOTES + "." + COLUMN_ID + " WHERE " + TABLE_NOTES_HASHTAGS + "." +
            COLUMN_HASHTAGS_ID + " = ?";

    private static final String REMOVE_HASHTAG_FROM_NOTE = COLUMN_NOTES_ID + " = ? AND " +
            COLUMN_HASHTAGS_ID + " = ?";

    private static final String MAX_NOTE_ID = "SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_NOTES;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        addDefaultHashtags();
    }

    private void addDefaultHashtags() {
        if(getHashtags().size() >= 3) return;
        for(DefaultHashtag defaultHashtag : DefaultHashtag.values()) {
            insertNewHashtag(new Hashtag(-1, defaultHashtag.getName()));
        }
    }

    List<Note> getNotes(int hashtagId) {
        if(hashtagId == ALL_NOTES) {
            return selectAllNotes();
        }
        return selectNotesForHashtag(hashtagId);
    }

    Note getNoteForId(int id) {
        for(Note note : selectAllNotes()) {
            if(note.getId() == id) return note;
        }
        return null;
    }

    private int getMaxNoteId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int maxId = 0;

        Cursor cur = db.rawQuery(MAX_NOTE_ID, null);
        if(cur.moveToFirst()) {
            do{
                maxId = Integer.parseInt(cur.getString(0));

            } while (cur.moveToNext());
        }
        cur.close();
        return maxId;
    }

    List<Hashtag> getHashtags() {
        return selectAllHashtags();
    }

    List<Hashtag> getHashtagsForNote(int noteId) {
        return selectHashtagsForNote(noteId);
    }


    private static Database instance;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_HASHTAGS_TABLE);
        db.execSQL(CREATE_NOTES_HASHTAGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ON_UPGRADE_DROP);
        onCreate(db);
    }

    static void init(Context context) {
        instance = new Database(context);
    }

    static Database getInstance(Context context) {
        if(instance == null) init(context);
        return instance;
    }

    private List<Note> selectAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> returnNotes = new ArrayList<>();

        Cursor cur = db.rawQuery(SELECT_ALL_NOTES, null);
        if(cur.moveToFirst()) {
            do{
                int id = Integer.parseInt(cur.getString(0));
                String title = cur.getString(1);
                String text = cur.getString(2);
                int color = Integer.parseInt(cur.getString(3));
                returnNotes.add(new Note(id,title,text, NoteColor.getNoteColorForId(color)));
            } while (cur.moveToNext());
        }
        cur.close();
        return returnNotes;
    }

    private List<Hashtag> selectAllHashtags() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Hashtag> returnHashtags = new ArrayList<>();

        Cursor cur = db.rawQuery(SELECT_ALL_HASHTAGS, null);
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

        Cursor cur = db.rawQuery(SELECT_HASHTAGS_FOR_NOTE, new String[] {String.valueOf(noteId)});
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

        Cursor cur = db.rawQuery(SELECT_NOTES_FOR_HASHTAG, new String[] {String.valueOf(hashtagId)});

        if(cur.moveToFirst()) {
            do{
                int id = Integer.parseInt(cur.getString(0));
                String title = cur.getString(1);
                String text = cur.getString(2);
                int color = Integer.parseInt(cur.getString(3));
                returnNotes.add(new Note(id,title,text,NoteColor.getNoteColorForId(color)));
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
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTES, null, values);

        return getMaxNoteId();
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
        db.delete(TABLE_NOTES_HASHTAGS, REMOVE_HASHTAG_FROM_NOTE, new String[] {String.valueOf(noteId), String.valueOf(hashtagId)});
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
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(note.getId())});
    }
}