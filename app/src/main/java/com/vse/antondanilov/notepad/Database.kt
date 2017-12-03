package com.vse.antondanilov.notepad

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


import java.util.ArrayList

import com.vse.antondanilov.notepad.Constants.ALL_NOTES

internal class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val hashtags: List<Hashtag>
        get() = selectAllHashtags()

    init {
        addDefaultHashtags()
    }

    private fun addDefaultHashtags() {
        if (hashtags.size >= 3) return
        for (defaultHashtag in DefaultHashtag.values()) {
            insertNewHashtag(Hashtag(-1, defaultHashtag.hashtagName))
        }
    }

    fun getNotes(hashtagId: Int): List<Note> {
        return if (hashtagId == ALL_NOTES) {
            selectAllNotes()
        } else selectNotesForHashtag(hashtagId)
    }

    fun getHashtagsForNote(noteId: Int): List<Hashtag> {
        return selectHashtagsForNote(noteId)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_NOTES_TABLE)
        db.execSQL(CREATE_HASHTAGS_TABLE)
        db.execSQL(CREATE_NOTES_HASHTAGS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(ON_UPGRADE_DROP)
        onCreate(db)
    }

    private fun selectAllNotes(): List<Note> {
        val db = this.readableDatabase
        val returnNotes = ArrayList<Note>()

        val cur = db.rawQuery(SELECT_ALL_NOTES, null)
        if (cur.moveToFirst()) {
            do {
                val id = Integer.parseInt(cur.getString(0))
                val title = cur.getString(1)
                val text = cur.getString(2)
                val color = Integer.parseInt(cur.getString(3))
                returnNotes.add(Note(id, title, text, NoteColor.getNoteColorForId(color)))
            } while (cur.moveToNext())
        }
        cur.close()
        return returnNotes
    }

    private fun selectAllHashtags(): List<Hashtag> {
        val db = this.readableDatabase
        val returnHashtags = ArrayList<Hashtag>()

        val cur = db.rawQuery(SELECT_ALL_HASHTAGS, null)
        if (cur.moveToFirst()) {
            do {
                val id = Integer.parseInt(cur.getString(0))
                val name = cur.getString(1)
                returnHashtags.add(Hashtag(id, name))
            } while (cur.moveToNext())
        }
        cur.close()
        return returnHashtags
    }

    private fun selectHashtagsForNote(noteId: Int): List<Hashtag> {
        val db = this.readableDatabase
        val returnHashtags = ArrayList<Hashtag>()

        val cur = db.rawQuery(SELECT_HASHTAGS_FOR_NOTE, arrayOf(noteId.toString()))
        if (cur.moveToFirst()) {
            do {
                val id = Integer.parseInt(cur.getString(0))
                val name = cur.getString(1)
                returnHashtags.add(Hashtag(id, name))
            } while (cur.moveToNext())
        }
        cur.close()
        return returnHashtags
    }

    private fun selectNotesForHashtag(hashtagId: Int): List<Note> {
        val db = this.readableDatabase
        val returnNotes = ArrayList<Note>()

        val cur = db.rawQuery(SELECT_NOTES_FOR_HASHTAG, arrayOf(hashtagId.toString()))

        if (cur.moveToFirst()) {
            do {
                val id = Integer.parseInt(cur.getString(0))
                val title = cur.getString(1)
                val text = cur.getString(2)
                val color = Integer.parseInt(cur.getString(3))
                returnNotes.add(Note(id, title, text, NoteColor.getNoteColorForId(color)))
            } while (cur.moveToNext())
        }
        cur.close()
        return returnNotes
    }

    fun insertNewNote(note: Note): Int {
        val values = ContentValues()
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_TEXT, note.text)
        values.put(COLUMN_COLOR, note.color!!.id)
        val db = writableDatabase
        db.insert(TABLE_NOTES, null, values)

        return selectAllNotes().size //==id of last insert
    }

    fun insertNewHashtag(hashtag: Hashtag) {
        val values = ContentValues()
        values.put(COLUMN_NAME, hashtag.hashtagName)
        val db = writableDatabase
        db.insert(TABLE_HASHTAGS, null, values)
    }

    fun addHashtagToNote(noteId: Int, hashtagId: Int) {
        val values = ContentValues()
        values.put(COLUMN_NOTES_ID, noteId)
        values.put(COLUMN_HASHTAGS_ID, hashtagId)
        val db = writableDatabase
        db.insert(TABLE_NOTES_HASHTAGS, null, values)
    }

    fun removeHashtagFromNote(noteId: Int, hashtagId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NOTES_HASHTAGS, REMOVE_HASHTAG_FROM_NOTE, arrayOf(noteId.toString(), hashtagId.toString()))
    }

    fun deleteHashtag(hashtagId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NOTES_HASHTAGS, COLUMN_HASHTAGS_ID + " = ?", arrayOf(hashtagId.toString()))
        db.delete(TABLE_HASHTAGS, COLUMN_ID + " = ?", arrayOf(hashtagId.toString()))
    }

    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NOTES_HASHTAGS, COLUMN_NOTES_ID + " = ?", arrayOf(noteId.toString()))
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", arrayOf(noteId.toString()))
    }

    fun updateNote(note: Note) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_TEXT, note.text)
        values.put(COLUMN_COLOR, note.color!!.id)
        val db = writableDatabase
        db.update(TABLE_NOTES, values, "id = ?", arrayOf(note.id.toString()))
    }

    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "notes_DB"
        private val TABLE_NOTES = "notes"
        private val TABLE_HASHTAGS = "hashtags"
        private val TABLE_NOTES_HASHTAGS = "notes_hashtags"
        private val COLUMN_ID = "id"
        private val COLUMN_NOTES_ID = "notes_id"
        private val COLUMN_HASHTAGS_ID = "hashtags_id"
        private val COLUMN_TITLE = "note_title"
        private val COLUMN_TEXT = "note_text"
        private val COLUMN_COLOR = "note_color"
        private val COLUMN_NAME = "hashtag_name"

        private val ON_UPGRADE_DROP = "DROP TABLE IF EXISTS " + TABLE_NOTES
        private val SELECT_ALL_NOTES = "SELECT * FROM " + TABLE_NOTES
        private val SELECT_ALL_HASHTAGS = "SELECT * FROM " + TABLE_HASHTAGS

        private val CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE + " TEXT," +
                COLUMN_TEXT + " TEXT," + COLUMN_COLOR + " INTEGER" + ")"

        private val CREATE_HASHTAGS_TABLE = "CREATE TABLE " + TABLE_HASHTAGS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT)"

        private val CREATE_NOTES_HASHTAGS_TABLE = ("CREATE TABLE " + TABLE_NOTES_HASHTAGS
                + "(" + COLUMN_NOTES_ID + " INTEGER," + COLUMN_HASHTAGS_ID + " INTEGER, FOREIGN KEY (" +
                COLUMN_NOTES_ID + ") REFERENCES " + TABLE_NOTES + "(" + COLUMN_ID + "), FOREIGN KEY (" +
                COLUMN_HASHTAGS_ID + ") REFERENCES " + TABLE_HASHTAGS +
                "(" + COLUMN_ID + "))")

        private val SELECT_HASHTAGS_FOR_NOTE = ("SELECT " + COLUMN_ID + ", " +
                COLUMN_NAME + " FROM " + TABLE_HASHTAGS + " JOIN " + TABLE_NOTES_HASHTAGS + " ON " +
                TABLE_NOTES_HASHTAGS + "." + COLUMN_HASHTAGS_ID + " = " + TABLE_HASHTAGS + "." + COLUMN_ID
                + " WHERE " + TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = ?")

        private val SELECT_NOTES_FOR_HASHTAG = ("SELECT * FROM " + TABLE_NOTES + " JOIN "
                + TABLE_NOTES_HASHTAGS + " ON " + TABLE_NOTES_HASHTAGS + "." + COLUMN_NOTES_ID + " = " +
                TABLE_NOTES + "." + COLUMN_ID + " WHERE " + TABLE_NOTES_HASHTAGS + "." +
                COLUMN_HASHTAGS_ID + " = ?")

        private val REMOVE_HASHTAG_FROM_NOTE = COLUMN_NOTES_ID + " = ? AND " +
                COLUMN_HASHTAGS_ID + " = ?"

        private lateinit var instance: Database

        fun init(context: Context) {
            instance = Database(context)
        }

        fun getInstance(context: Context): Database {
            init(context)
            return instance
        }
    }
}