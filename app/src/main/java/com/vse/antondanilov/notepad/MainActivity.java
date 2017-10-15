package com.vse.antondanilov.notepad;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

     generateTableItems();
    }

    private void generateTableItems() {
        TableLayout table = (TableLayout) this.findViewById(R.id.main_menu_table);

        for(int i = 0; i < 25; i++) {
            LinearLayout tableRow = (LinearLayout) View.inflate(this, R.layout.main_menu_item, null);

            Random rand = new Random();
            int k = rand.nextInt(9);

            tableRow.setBackgroundColor(Color.parseColor("#" + (i%10) + (i%10) + k + k + k + k));

            TextView title = (TextView)tableRow.findViewById(R.id.note_title);
            TextView noteText = (TextView)tableRow.findViewById(R.id.note_text);


            noteText.setPadding(noteText.getPaddingLeft(), 0,0, k* 50);
            title.setText("This is item no.:" + (i+1));
            String t = i % 2 == 0 ? "Bureš" : "Eman";
            noteText.setText(t);

            table.addView(tableRow);
        }

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
}
