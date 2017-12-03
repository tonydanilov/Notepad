package com.vse.antondanilov.notepad

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout

import com.vse.antondanilov.notepad.Constants.NEW_HASHTAG_DEFAULT_VALUE
import com.vse.antondanilov.notepad.Constants.NOTE_ID
import java.util.ArrayList

class HashtagsActivity : AppCompatActivity() {

    private var table: TableLayout? = null
    private var noteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hashtags)
        val toolbar = findViewById(R.id.toolbar_hashtags) as Toolbar
        setSupportActionBar(toolbar)

        noteId = intent.extras.getInt(NOTE_ID)
        loadHashtags()
    }

    private fun loadHashtags() {
        table = this.findViewById(R.id.hashtags_table) as TableLayout
        val hashtags = Database.getInstance(this)!!.hashtags
        for (hashtag in hashtags) {
            table!!.addView(createCheckbox(hashtag))
        }

        val addButtonLayout = View.inflate(this, R.layout.item_new_hashtag, null) as LinearLayout
        val addHashtagButton = addButtonLayout.findViewById<Button>(R.id.add_hashtag_button)
        addHashtagButton.setOnClickListener { createAddHashtagDialog() }
        table!!.addView(addButtonLayout)
    }

    private fun createCheckbox(hashtag: Hashtag): LinearLayout {
        val tableRow = View.inflate(this, R.layout.item_hashtags, null) as LinearLayout
        val hashtagCheckbox = tableRow.findViewById<CheckBox>(R.id.hashtag_checkbox)
        hashtagCheckbox.text = hashtag.hashtagName
        hashtagCheckbox.isChecked = isNotesHashtag(noteId, hashtag.id)
        hashtagCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveHashtag(hashtag.id)
            } else {
                removeHashtagFromNote(hashtag.id)
            }
        }

        val deleteHashtagClickListener = View.OnLongClickListener {
            creatDeleteDialog(hashtag)
            false
        }

        hashtagCheckbox.setOnLongClickListener(deleteHashtagClickListener)
        tableRow.setOnLongClickListener(deleteHashtagClickListener)

        return tableRow
    }

    /**
     * Function determine if hashtag belong to note
     * @param noteId identification number of note
     * @param hashtagId identification number of hashtag
     * @return Boolean value of true or false expressing belonginess of hashtag to note
     */
    private fun isNotesHashtag(noteId: Int, hashtagId: Int): Boolean {
        return Database.getInstance(this).getHashtagsForNote(noteId).any { hashtagId == it.id }
    }

    private fun refreshHashtags() {
        table!!.removeAllViews() //removing all views with hashtag from table (list)
        loadHashtags()
    }

    private fun createAddHashtagDialog() {
        val builder = AlertDialog.Builder(this@HashtagsActivity)
        builder.setTitle(getString(R.string.new_hashtag))
        val input = EditText(this@HashtagsActivity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setLinkTextColor(resources.getColor(R.color.colorPrimary))
        input.postDelayed({
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(input, 0)
        }, 50)
        builder.setView(input)

        /*
        setting positive button listener for confirming addition of hashtag to note
         */
        builder.setPositiveButton(R.string.dialog_ok_button) { _, _ ->
            Database.getInstance(this@HashtagsActivity).insertNewHashtag(Hashtag(NEW_HASHTAG_DEFAULT_VALUE, input.text.toString()))
            refreshHashtags()
        }

        builder.setNegativeButton(getString(R.string.dialog_cancel_button)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun creatDeleteDialog(hashtag: Hashtag) {
        val builder = AlertDialog.Builder(this@HashtagsActivity)
        builder.setTitle(getString(R.string.dialog_delete_question_hashtag) + " \"" + hashtag.hashtagName + "\"?")
        builder.setPositiveButton(getString(R.string.dialog_delete_button)) { _, _ ->
            deleteHashtag(hashtag.id)
            refreshHashtags()
        }

        builder.setNegativeButton(getString(R.string.dialog_cancel_button)) { dialog , _ -> dialog.cancel() }
        builder.show()
    }

    private fun saveHashtag(hashtagId: Int) {
        Database.getInstance(this).addHashtagToNote(noteId, hashtagId)
    }

    private fun deleteHashtag(hashtagId: Int) {
        Database.getInstance(this).deleteHashtag(hashtagId)
    }

    private fun removeHashtagFromNote(hashtagId: Int) {
        Database.getInstance(this).removeHashtagFromNote(noteId, hashtagId)
    }
}
