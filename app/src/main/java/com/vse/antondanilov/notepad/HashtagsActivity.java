package com.vse.antondanilov.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import java.util.List;

import static com.vse.antondanilov.notepad.MainActivity.NOTE_ID;

public class HashtagsActivity extends AppCompatActivity {

    private TableLayout table;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_hashtags);
        setSupportActionBar(toolbar);

        noteId = getIntent().getExtras().getInt(NOTE_ID);
        loadHashtags();
    }

    private void loadHashtags() {
        table = (TableLayout) this.findViewById(R.id.hashtags_table);
        List<Hashtag> hashtags = MainActivity.getDB().getHashtags();
        for(final Hashtag hashtag : hashtags) {
            LinearLayout tableRow = (LinearLayout) View.inflate(this, R.layout.hashtags_item, null);

            //TODO svoje metoda
            CheckBox hashtagCheckbox = tableRow.findViewById(R.id.hashtag_checkbox);
            hashtagCheckbox.setText(hashtag.getName());
            hashtagCheckbox.setChecked(isNotesHashtag(noteId, hashtag.getId()));
            hashtagCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        saveHashtag(hashtag.getId());
                    } else {
                        removeHashtagFromNote(hashtag.getId());
                    }

                }
            });

            table.addView(tableRow);

            View.OnLongClickListener deleteHashtagClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    creatDeleteDialog(hashtag);
                    return false;
                }
            };

            hashtagCheckbox.setOnLongClickListener(deleteHashtagClickListener);
            tableRow.setOnLongClickListener(deleteHashtagClickListener);
        }

        LinearLayout addButtonLayout = (LinearLayout) View.inflate(this, R.layout.new_hashtags_item, null);
        Button addHashtagButton = addButtonLayout.findViewById(R.id.add_hashtag_button);
        addHashtagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddHashtagDialog();
            }
        });
        table.addView(addButtonLayout);
    }



    private boolean isNotesHashtag(int noteId, int hashtagId) {
        for(Hashtag hashtag : MainActivity.getDB().getHashtagsForNote(noteId)) {
            if(hashtagId == hashtag.getId()) return true;
        }
        return false;
    }

    private void refreshHashtags() {
        table.removeAllViews();
        loadHashtags();
    }

    //TODO to R.string
    private void createAddHashtagDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HashtagsActivity.this);
        builder.setTitle(getString(R.string.new_hashtag));
        final EditText input = new EditText(HashtagsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLinkTextColor(getResources().getColor(R.color.colorPrimary));
        input.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(input, 0);
            }
        }, 50);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.getDB().insertNewHashtag(new Hashtag(-1, input.getText().toString()));
                refreshHashtags();
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

    //TODO to R.string
    private void creatDeleteDialog(final Hashtag hashtag) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HashtagsActivity.this);
        builder.setTitle("Delete hashtag " + hashtag.getName() + "?");  //TODO
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteHashtag(hashtag.getId());
                refreshHashtags();
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

    private void saveHashtag(int hashtagId) {
        MainActivity.getDB().addHashtagToNote(noteId, hashtagId);
    }

    private void deleteHashtag(int hashtagId) {
        MainActivity.getDB().deleteHashtag(hashtagId);
    }

    private void removeHashtagFromNote(int hashtagId) {
        MainActivity.getDB().removeHashtagFromNote(noteId, hashtagId);
    }
}
