package com.tyroneil.longshootalpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.google.android.material.checkbox.MaterialCheckBox;

public class SequenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);
        Toolbar toolbar_activity_sequence = (Toolbar) findViewById(R.id.toolbar_activity_sequence);
        setSupportActionBar(toolbar_activity_sequence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_sequence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_sequence_start:
                // TODO: start the sequence
                return true;

            case R.id.action_sequence_loop:
                // TODO: set this sequence on loop
                menuItem.setChecked(! menuItem.isChecked());
                return false;

            case R.id.action_sequence_clear:
                // TODO: clear everything in the sequence
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
