package com.tyroneil.longshootalpha;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // setup ActionBar
        Toolbar toolbar_activity_settings = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        setSupportActionBar(toolbar_activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup preference content
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings_about:
                // TODO: show 'about app' information
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
